package com.landerer.openshift;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/hello-world")
public class HelloWorldRestController {

    @GetMapping
    public ResponseEntity<HelloWorldDTO> getHelloWorld(@RequestParam final String name) {
        final HelloWorldDTO dto = new HelloWorldDTO();
        dto.setMessage("Hello, " + name + "!");
        dto.setUuid(UUID.randomUUID().toString());
        return ResponseEntity.ok(dto);
    }

    public static final class HelloWorldDTO {

        private String message;
        private String uuid;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }
    }
}
