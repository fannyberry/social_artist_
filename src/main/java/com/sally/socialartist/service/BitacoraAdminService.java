package com.sally.socialartist.service;

import com.sally.socialartist.model.BitacoraAdmin;
import com.sally.socialartist.model.Usuario;
import com.sally.socialartist.repository.BitacoraAdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BitacoraAdminService {

    private final BitacoraAdminRepository bitacoraAdminRepository;

    public void registrar(Usuario admin, String accion, String detalle) {
        BitacoraAdmin registro = new BitacoraAdmin();
        registro.setAdmin(admin);
        registro.setAccion(accion);
        registro.setDetalle(detalle);
        bitacoraAdminRepository.save(registro);
    }
}
