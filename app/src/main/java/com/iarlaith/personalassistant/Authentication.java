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

    public void addAuthentication(String email, String password){
        authenticationMapper.put(email, password);
    }

    public boolean checkEmail(String email){
        return authenticationMapper.containsKey(email);
    }

    public boolean verifyAuthentication(String email, String password){
        if(authenticationMapper.containsKey(email)){
            return password.equals(authenticationMapper.get(email));
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
