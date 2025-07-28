package com.cyctius.controller;

import com.cyctius.dto.CyctiusUserDTO;
import com.cyctius.dto.UpdateUserDataRequestDTO;
import com.cyctius.service.UserDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDatController {

    private final UserDataService userDataService;

    @PostMapping("/update")
    ResponseEntity<CyctiusUserDTO> updateUserData(@RequestBody UpdateUserDataRequestDTO userDTO) {
        return ResponseEntity.ok(userDataService.updateUserData(userDTO));
    }

    @GetMapping("/me")
    ResponseEntity<CyctiusUserDTO> getCurrentUserData() {
        return ResponseEntity.ok(userDataService.currentUserData());
    }
}
