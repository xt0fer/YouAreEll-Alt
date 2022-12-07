package main

import (
	"fmt"
	prompt "github.com/c-bata/go-prompt"
	cont "github.com/woat/youareell/controllers"
	out "github.com/woat/youareell/output"
	srv "github.com/woat/youareell/service"
	"strings"
)

func completer(d prompt.Document) []prompt.Suggest {
	if len(strings.Split(d.GetWordBeforeCursorUntilSeparator(">"), " ")) > 2 {
		return nil
	}
	s := []prompt.Suggest{
		{Text: "ids", Description: "[<your_name> <github_id]"},
		{Text: "messages", Description: "[<github_id>]"},
		{Text: "send", Description: "{<your_name> <message_body> | <your_name> <message_body> to <your_friend>}"},
	}
	return prompt.FilterHasPrefix(s, d.GetWordBeforeCursor(), true)
}

func start(origin chan bool) {
	for {
		x := prompt.Input(">>> ", completer,
			prompt.OptionPrefixTextColor(prompt.Cyan),
			prompt.OptionSuggestionBGColor(prompt.Red),
			prompt.OptionDescriptionBGColor(prompt.Green),
			prompt.OptionSelectedSuggestionBGColor(prompt.DarkRed),
			prompt.OptionSuggestionTextColor(prompt.Black),
			prompt.OptionSelectedDescriptionBGColor(prompt.DarkGreen),
			prompt.OptionPreviewSuggestionTextColor(prompt.Red),
		)
		s := out.ParseArgs(x)
		if len(s) == 0 {
			continue
		}
		if s[0] == "ids" {
			// Post the NAME :: GITHUB
			if len(s) > 1 {
				cont.PostOrPutIds(s[1], s[2])
				continue
			}
			// Get DBs if they didn't pass anything more.
			out.PrintUsers(cont.GetIds())
			continue
		}
		if s[0] == "messages" {
			// Get id messages
			if len(s) == 2 {
				out.PrintMsgs(cont.GetMsgById(s[1]))
				continue
			}
			out.PrintMsgs(cont.GetMsg())
			continue
		}
		if s[0] == "send" {
			// Send to main timeline
			if len(s) == 3 {
				cont.PostSend(s[1], s[2])
				origin <- true
			}
			// Send to another github id
			if len(s) == 5 {
				cont.PostSendToGithub(s[1], s[4], s[2])
				origin <- true
			}
			continue
		}
		if s[0] == "cmd" {
			fmt.Printf("Available commands:\n")
			fmt.Printf("%5s ids [<your_name> <github_id>]\n", "")
			fmt.Printf("%5s messages [<github_id>]\n", "")
			fmt.Printf("%5s send {<your_name> <message_body> | <your_name> <message_body> to <your_friend>}\n", "")
			continue
		}
		if s[0] == "quit" {
			break
		}
		fmt.Printf("Command: %s not recognized. Type \"cmd\" for a list of commands.\n", s)
	}
}

func main() {
	origin := make(chan bool, 1)
	go srv.NotificationService(origin)
	start(origin)
}
