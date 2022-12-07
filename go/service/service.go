package service

import (
	"fmt"
	c "github.com/woat/youareell/controllers"
	"time"
)

var last []byte

func SetLastBytes() {
	_, bytes := c.ServeMsg(last)
	last = bytes
}

func NotificationService(originIp chan bool) {
	SetLastBytes()
	for {
		select {
		case <-originIp:
			_, bytes := c.ServeMsg(last)
			if bytes == nil {
				continue
			}
			fmt.Print("\033[s")
			fmt.Print("Message sent.")
			fmt.Print("\033[u")
			last = bytes
			continue
		default:
		}

		_, bytes := c.ServeMsg(last)
		if bytes == nil {
			time.Sleep(time.Second * 2)
			continue
		}
		last = bytes
		fmt.Print("\033[s")
		fmt.Print("\033[A\r" + "New unread message!")
		fmt.Print("\033[u")
		time.Sleep(time.Second * 2)
	}
}
