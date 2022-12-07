package output

import (
	c "github.com/woat/youareell/controllers"
	"testing"
)

type Msg struct {
	Sequence  string `json:"sequence"`
	Timestamp string `json:"timestamp"`
	Fromid    string `json:"fromid"`
	Toid      string `json:"toid"`
	Message   string `json:"message"`
}

func TestParseArgs(t *testing.T) {
	var stdargs = []struct {
		in       string
		expected int
	}{
		{"ids", 1},
		{"messages your_github_id", 2},
		{"ids your_name your_github_id", 3},
		{"send your_github_id 'my string message' to some_friend_githubid", 5},
	}
	for _, arg := range stdargs {
		actual := len(ParseArgs(arg.in))
		if actual != arg.expected {
			t.Errorf("ParseArgs(%s) got: %v, want: %v => arr: %v", arg.in, actual, arg.expected, ParseArgs(arg.in)[3])
		}
	}
}

func TestParseMsg(t *testing.T) {
	var stdargs = []struct {
		in       c.Msg
		expected string
	}{
		{c.Msg{Fromid: "fromid", Toid: "toid", Message: "msg"}, "fromid: @toid msg"},
		{c.Msg{Fromid: "sameid", Toid: "sameid", Message: "msg"}, "sameid: msg"},
		{c.Msg{Fromid: "sameid", Toid: "", Message: "msg"}, "sameid: msg"},
	}
	for _, arg := range stdargs {
		actual := ParseMsg(arg.in)
		if actual != arg.expected {
			t.Errorf("ParseMsg(%v) got: '%v', want: '%v'", arg.in, actual, arg.expected)
		}
	}
}

func TestParseUser(t *testing.T) {
	var stdargs = []struct {
		in       c.User
		expected string
	}{
		{c.User{Name: "test", Github: "test"}, "Name: test - Github: test"},
	}

	for _, arg := range stdargs {
		actual := ParseUser(arg.in)
		if actual != arg.expected {
			t.Errorf("ParseUser(%v) got: '%v', want: '%v'", arg.in, actual, arg.expected)
		}
	}
}

func TestPrintUser(t *testing.T) {
	var stdargs = []struct {
		in       []c.User
		expected string
	}{
		{[]c.User{{Name: "test", Github: "test"}}, "Name: test - Github: test\n"},
		{[]c.User{{Name: "test", Github: "test"}, {Name: "test1", Github: "test1"}}, "Name: test - Github: test\nName: test1 - Github: test1\n"},
	}

	for _, arg := range stdargs {
		actual := PrintUsers(arg.in)
		if actual != arg.expected {
			t.Errorf("ParseUser(%v) got: '%v', want: '%v'", arg.in, actual, arg.expected)
		}
	}
}
