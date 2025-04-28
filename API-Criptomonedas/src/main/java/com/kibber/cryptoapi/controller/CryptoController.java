package com.kibber.cryptoapi.controller;


import com.kibber.cryptoapi.model.Criptomoneda;
import com.kibber.cryptoapi.service.CryptoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/criptomonedas")
public class CryptoController {

    @Autowired
    private CryptoService cryptoService;

    // todos los metodos que quiero que se puedan ejecutar desde la URL
    //Esto es un endpoint
    @GetMapping("/getAll")
    public ResponseEntity<List<Criptomoneda>> getCrypto(){
        return new ResponseEntity<>(cryptoService.getAllCriptomonedas(), HttpStatus.OK);
    }

    @GetMapping("/top10Caras")
    public ResponseEntity<List<Criptomoneda>> getTop10Caras(){
        return new ResponseEntity<>(cryptoService.getTop10ByOrderByPrecioActualDesc(), HttpStatus.OK);
    }

    @GetMapping("/top10Subidas")
    public ResponseEntity<List<Criptomoneda>> getTop10MasSubieron(){
        return new ResponseEntity<>(cryptoService.getTop10MasHanSubido(), HttpStatus.OK);
    }

    @GetMapping("/top10Bajadas")
    public ResponseEntity<List<Criptomoneda>> getTop10MasBajaron(){
        return new ResponseEntity<>(cryptoService.getTop10MasHanBajado(), HttpStatus.OK);
    }

    @GetMapping("/top10Volumen")
    public ResponseEntity<List<Criptomoneda>> getTop10VolumenMercado(){
        return new ResponseEntity<>(cryptoService.getTop10MasVolumenMercado(), HttpStatus.OK);
    }

    @GetMapping("/actualizar")
    public ResponseEntity<String> actualizarCriptos() {
        cryptoService.actualizarCriptomonedasDesdeCoinGecko(); // 👈 Llama al nombre correcto
        return new ResponseEntity<>("Criptomonedas actualizadas correctamente desde CoinGecko.", HttpStatus.OK);
    }

}
