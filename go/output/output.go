package output

import (
	"bytes"
	"fmt"
	c "github.com/woat/youareell/controllers"
	"regexp"
)

func ParseArgs(s string) []string {
	r := regexp.MustCompile("'.+'|\".+\"|\\S+")
	arr := r.FindAllString(s, -1)
	return arr
}

func ParseMsg(m c.Msg) string {
	var pm string
	if m.Fromid == m.Toid || m.Toid == "" {
		pm = fmt.Sprintf("%v: %v", m.Fromid, m.Message)
	} else {
		pm = fmt.Sprintf("%v: @%v %v", m.Fromid, m.Toid, m.Message)
	}
	return pm
}

func ParseUser(u c.User) string {
	pu := fmt.Sprintf("Name: %v - Github: %v", u.Name, u.Github)
	return pu
}

func PrintUsers(u []c.User) string {
	var buffer bytes.Buffer
	for _, e := range u {
		buffer.WriteString(ParseUser(e) + "\n")
	}

	fmt.Println(buffer.String())
	return buffer.String()
}

func PrintMsgs(m []c.Msg) string {
	var buffer bytes.Buffer
	for _, e := range m {
		buffer.WriteString(ParseMsg(e) + "\n")
	}

	fmt.Println(buffer.String())
	return buffer.String()
}
