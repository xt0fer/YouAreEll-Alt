package controllers

import (
	"bytes"
	"encoding/json"
	"fmt"
	"io/ioutil"
	"log"
	"net/http"
)

type User struct {
	Userid string `json:"userid"`
	Name   string `json:"name"`
	Github string `json:"github"`
}

type Msg struct {
	Sequence  string `json:"sequence,omitempty"`
	Timestamp string `json:"timestamp,omitempty"`
	Fromid    string `json:"fromid"`
	Toid      string `json:"toid"`
	Message   string `json:"message"`
}

// All HTTP methods will take a slug and return a []byte.
// The specific utility handlers will take the []byte and
// marshal them to the proper struct.

func get(slug string) []byte {
	url := fmt.Sprintf("http://zipcode.rocks:8085/%s", slug)
	res, err := http.Get(url)
	if err != nil {
		log.Fatal(err)
	}
	defer res.Body.Close()

	resData, err := ioutil.ReadAll(res.Body)
	if err != nil {
		log.Fatal(err)
	}

	return resData
}

func post(slug string, data []byte) {
	url := fmt.Sprintf("http://zipcode.rocks:8085/%s", slug)
	res, err := http.Post(url, "application/json", bytes.NewBuffer(data))
	if err != nil {
		log.Fatal(err)
	}
	defer res.Body.Close()
}

func put(slug string, data []byte) {
	var client = &http.Client{}
	url := fmt.Sprintf("http://zipcode.rocks:8085/%s", slug)
	req, err := http.NewRequest(http.MethodPut, url, bytes.NewBuffer(data))
	if err != nil {
		log.Fatal(err)
	}
	res, err := client.Do(req)
	if err != nil {
		log.Fatal(err)
	}
	defer res.Body.Close()
}

func doesUserExist(github string) bool {
	resBody := get("ids/" + github)
	s := string(resBody[:])
	// A length of 2 denotes a user not found.
	return len(s) != 2
}

// ids handlers
func GetIds() []User {
	resData := get("ids")
	var users []User
	json.Unmarshal(resData, &users)
	return users
}

func PostOrPutIds(name, github string) {
	if doesUserExist(github) {
		PutIds(name, github)
	} else {
		PostIds(name, github)
	}
}

func PostIds(name, github string) {
	user := User{"", name, github}
	j, err := json.Marshal(&user)
	if err != nil {
		log.Fatal(err)
	}
	post("ids", j)
}

func PutIds(name, github string) {
	res := get("ids/" + github)
	var id string
	json.Unmarshal(res, &id)
	user := User{id, name, github}
	j, err := json.Marshal(&user)
	if err != nil {
		log.Fatal(err)
	}
	put("ids", j)
}

// messages handlers
func GetMsg() []Msg {
	resData := get("messages")
	var msgs []Msg
	json.Unmarshal(resData, &msgs)
	return msgs
}

func ServeMsg(last []byte) ([]Msg, []byte) {
	resData := get("messages")
	if bytes.Equal(resData, last) {
		return nil, nil
	}
	var msgs []Msg
	json.Unmarshal(resData, &msgs)
	return msgs, resData
}

func GetMsgById(github string) []Msg {
	resData := get("ids/" + github + "/messages")
	var msgs []Msg
	json.Unmarshal(resData, &msgs)
	return msgs
}

// send handlers
func PostSend(github, message string) {
	m := Msg{Fromid: github, Toid: "", Message: message}
	j, err := json.Marshal(&m)
	if err != nil {
		log.Fatal(err)
	}
	post("ids/"+github+"/messages", j)
}

func PostSendToGithub(fromGithub, toGithub, message string) {
	m := Msg{Fromid: fromGithub, Toid: toGithub, Message: message}
	j, err := json.Marshal(&m)
	if err != nil {
		log.Fatal(err)
	}
	post("ids/"+fromGithub+"/messages", j)
}
