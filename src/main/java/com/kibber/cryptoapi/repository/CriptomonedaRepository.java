package com.kibber.cryptoapi.repository;

import com.kibber.cryptoapi.model.Criptomoneda;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CriptomonedaRepository extends JpaRepository <Criptomoneda, Integer> {


    List<Criptomoneda> findTop10ByOrderByPrecioActualDesc();
    List<Criptomoneda> findTop10ByOrderByCambio24hDesc();
    List<Criptomoneda> findTop10ByOrderByCambio24hAsc();
    List<Criptomoneda> findTop10ByOrderByVolumenMercadoDesc();

}
