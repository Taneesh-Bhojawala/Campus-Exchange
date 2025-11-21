//ClaimRequest.java
package com.campus.exchange.dto;

import lombok.Data;

//Here lombok generates all the getters, setters, etc. We dont need to do this manually.
//@Data annotation helps in that, lombok auto-generates this at compile time
@Data
public class ClaimRequest
{
    private String itemId;
    private String claimerId;
    private String listerId;
}
