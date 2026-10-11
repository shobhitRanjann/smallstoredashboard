package com.tambolaapp.tambola.homecontroller;

import com.tambolaapp.tambola.dto.*;
import com.tambolaapp.tambola.entity.PlayerGeneratedValues;
import com.tambolaapp.tambola.service.AuthenticationService;
import com.tambolaapp.tambola.service.JWTService;
import com.tambolaapp.tambola.service.SaveData;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Log4j2
//@CrossOrigin(origins = "https://smallstore.in/", allowedHeaders = "*", allowCredentials = "true")
@RequestMapping("/api")
public class TambolaController {

    public TambolaController() {
    }

    @Autowired
    private AuthenticationService authenticationService;

    public TambolaController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Autowired
    private JWTService jwtService;

    public TambolaController(JWTService jwtService) {
        this.jwtService = jwtService;
    }

    @Autowired
    private SaveData saveData;

    public TambolaController(SaveData saveData) {
        this.saveData = saveData;
    }

    @PostMapping("/host")
    public ResponseEntity<?> updateHostId(@RequestBody HostIdCreateRequest hostIdCreateRequest){

        log.info(hostIdCreateRequest.getToken());
        log.info("RoomId >>  "+hostIdCreateRequest.getRoomidforhost());
        String username =  jwtService.extractUserName(hostIdCreateRequest.getToken());
        log.info("Username found  > "+username);
        saveData.updateLinkOfHost(username, hostIdCreateRequest.getRoomidforhost());
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("Invalid credentials"));
        }
        return ResponseEntity.ok(new UserEmailFoundResponse(username));
    }
    @PostMapping("/player")
    public ResponseEntity<?> checkHostId(@RequestBody HostIdCheck urlid){
        String checkifexist="";
        if(urlid.getHostid()!="" || urlid.getHostid() !=null){
            log.info("RoomId >>  "+urlid.getHostid());
             checkifexist = saveData.checkIfExists(urlid);
        }

        log.info("id Found >> "+checkifexist);
        return ResponseEntity.ok(new StatusReponse(checkifexist));
    }

    @PostMapping("/submitname")
    public ResponseEntity<?> savePlayerName(@RequestBody GetUserNameRequest getUserNameRequest){

        GetUserNameResponse userNameResponse=  saveData.savePlayerName(getUserNameRequest);
        return ResponseEntity.ok(userNameResponse);
    }
    @PostMapping("/updatevalues")
    public ResponseEntity<?> updatePlayerValue(@RequestBody List<DataItemRequest> dataItemRequest){
        log.info(dataItemRequest.get(0).getUserid());
        if(dataItemRequest.get(0).getUserid() != ""){
            saveData.createPlayerValue(dataItemRequest);
        }
        return ResponseEntity.ok(new StatusReponse("success"));
    }

    @PostMapping("/updatevaluesag")
    public ResponseEntity<?> updatePlayerValueag(@RequestBody List<DataItemRequest> dataItemRequest){
        log.info(dataItemRequest);
        saveData.updatelayerValue(dataItemRequest);
        return ResponseEntity.ok(new StatusReponse("success"));
    }
    @PostMapping("/updatevaluesafe")
    public ResponseEntity<?> updatevaluesafe(@RequestBody DataSingleItem dataSingleItem){
        log.info(dataSingleItem);
        saveData.updatevaluesafe(dataSingleItem);
        return ResponseEntity.ok(new StatusReponse("success"));
    }

    @GetMapping("/getbyuserid")
    public ResponseEntity<?> getPlayerValues(@RequestParam Long playerid){
        List<PlayerGeneratedValues> playerGeneratedValues = saveData.playerGeneratedValues(playerid);
        return ResponseEntity.ok(playerGeneratedValues);
    }

    @GetMapping("/getplayerdetails")
    public ResponseEntity<?> getPlayerDetails(@RequestParam Long playerid){
        GetUserNameResponse response = saveData.getPlayerDetails(playerid);
        if (response != null) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new StatusReponse("not found"));
        }
    }
}
