package com.sally.socialartist.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MeGustaResponseDTO {

    private boolean meGusta; // si el usuario en sesion ya dio me gusta
    private long total;
}
