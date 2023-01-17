package com.iarlaith.personalassistant;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Authentication implements Serializable {

    public HashMap<String, String> getAuthenticationMapper() {
        return authenticationMapper;
    }

    public void setAuthenticationMapper(HashMap<String, String> authenticationMapper) {
        this.authenticationMapper = authenticationMapper;
    }

    HashMap<String, String> authenticationMapper = new HashMap<String, String>();

    public void addAuthentication(String username, String password){
        authenticationMapper.put(username, password);
    }

    public boolean checkUsername(String username){
        return authenticationMapper.containsKey(username);
    }

    public boolean verifyAuthentication(String username, String password){
        if(authenticationMapper.containsKey(username)){
            return password.equals(authenticationMapper.get(username));
        }
        return false;
    }

    public void loadAuthenications(Map<String, ?> spMap) { //Shared Preferences
        for(Map.Entry<String, ?> entries : spMap.entrySet()){
            if(!entries.getKey().equals("RememberMeCB")){
                authenticationMapper.put(entries.getKey(), entries.getValue().toString());
            }
        }
    }
}
