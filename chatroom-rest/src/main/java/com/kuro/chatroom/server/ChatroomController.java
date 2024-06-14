package com.kuro.chatroom.server;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kuro.chatroom.Message;

@RestController
@RequestMapping("/api")
public class ChatroomController {

    private final List<Message> messages = new ArrayList<>();
    private final Set<String> subscribedUsers = new HashSet<>();
    private final List<Instant> instants = new ArrayList<>();

    @GetMapping("/subscribe/{pseudo}")
    public ResponseEntity<String> subscribe(@PathVariable("pseudo") String username) {
        if (subscribedUsers.contains(username)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already taken");
        }
        subscribedUsers.add(username);
        return ResponseEntity.ok("Connected");
    }

    @GetMapping("/unsubscribe/{pseudo}")
    public ResponseEntity<String> unsubscribe(@PathVariable("pseudo") String pseudo) {
        if (!subscribedUsers.contains(pseudo)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        subscribedUsers.remove(pseudo);
        return ResponseEntity.ok("Disconnected");
    }

    @PostMapping("/messages")
    public ResponseEntity<Void> postMessage(@RequestBody Message message) {
        if (!subscribedUsers.contains(message.getPseudo()) && !message.getPseudo().equals("INFO")) {
            System.out.println("User not subscribed to the chat room");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        messages.add(message);
        instants.add(Instant.now());

        System.out.println("Message received: " + message.getContent());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/messages")
    public ResponseEntity<List<Message>> getMessages(
            @RequestParam("bef") Date before,
            @RequestParam(value = "time", required = false) long time) {

        if (time == 0) {
            return ResponseEntity.ok(messages);
        }
        List<Message> messagesToSend = new ArrayList<>();
        for (int i = 0; i < instants.size(); i++) {
            System.out.println(
                    "before: " + (messages.get(i).getSentAt().getTime() < before.getTime()) + " time: " + " instants: "
                            + (instants.get(i).toEpochMilli() > time) + " messages: " + messages.get(i).getContent());
            if (instants.get(i).toEpochMilli() > time && messages.get(i).getSentAt().getTime() > before.getTime()) {
                messagesToSend.add(messages.get(i));
            }
        }
        return ResponseEntity.ok(messagesToSend);
    }
}