package com.kibber.cryptoapi.service;

import com.kibber.cryptoapi.dto.CryptoDTO;
import com.kibber.cryptoapi.model.Criptomoneda;
import com.kibber.cryptoapi.repository.CriptomonedaRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CryptoService {

    private final CriptomonedaRepository criptomonedaRepository;
    private final RestTemplate restTemplate;
    private static final Logger logger = LoggerFactory.getLogger(CryptoService.class);

    public CryptoService(CriptomonedaRepository criptomonedaRepository, RestTemplate restTemplate) { //CONSTRUCTOR DEBE SER PÚBLICO
        this.criptomonedaRepository = criptomonedaRepository;
        this.restTemplate = restTemplate;
    }

    public List<Criptomoneda> getAllCriptomonedas() { //ME DA TODAS LAS CRYPTOS
        return criptomonedaRepository.findAll();
    }

    public List<Criptomoneda> getTop10ByOrderByPrecioActualDesc() { //ME DA LAS CRYPTOS CON SU PRECIO ACTUAL DESCENDIENTE

        return criptomonedaRepository.findTop10ByOrderByPrecioActualDesc();
    }

    public List<Criptomoneda> getTop10MasHanSubido() { //ME DA LAS CRYPTOS QUE MÁS HAN SUBIDO DES

        return criptomonedaRepository.findTop10ByOrderByCambio24hDesc();
    }

    public List<Criptomoneda> getTop10MasHanBajado() { //ME DA LAS CRYPTOS QUE MÁS HAN BAJADO ASCENDIENTE

        return criptomonedaRepository.findTop10ByOrderByCambio24hAsc();
    }

    public List<Criptomoneda> getTop10MasVolumenMercado() { //ME DA LAS CRYPTOS CON MAYOR VOLUMEN DE MERCADO

        return criptomonedaRepository.findTop10ByOrderByVolumenMercadoDesc();
    }

    @Scheduled(fixedRate = 3600000) // cada hora
    public void actualizarCriptomonedasDesdeCoinGecko() {
        logger.info("🛠️ Actualizando criptomonedas desde CoinGecko...");

        try {
            String url = "https://api.coingecko.com/api/v3/coins/markets?vs_currency=eur&order=market_cap_desc&per_page=10&page=1&sparkline=false";

            CryptoDTO[] cryptos = restTemplate.getForObject(url, CryptoDTO[].class);

            if (cryptos != null && cryptos.length > 0) {
                criptomonedaRepository.deleteAll();
                Arrays.stream(cryptos).forEach(cryptoDTO -> {
                    Criptomoneda cripto = new Criptomoneda(
                            0,
                            cryptoDTO.getName(),
                            cryptoDTO.getSymbol(),
                            cryptoDTO.getCurrentPrice(),
                            cryptoDTO.getPriceChangePercentage24h(),
                            cryptoDTO.getMarketCap(),
                            LocalDate.now()
                    );
                    criptomonedaRepository.save(cripto);
                });
                logger.info("✅ Actualización completada correctamente.");
            } else {
                logger.warn("⚠️ No se recibieron datos de CoinGecko.");
            }

        } catch (Exception e) {
            logger.error("❌ Error al actualizar criptomonedas: " + e.getMessage());
        }
    }
}


