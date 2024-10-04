package MeiaLuaQuadrado.ventz.adapters.out.controller;

import MeiaLuaQuadrado.ventz.adapters.in.Ingresso;
import MeiaLuaQuadrado.ventz.adapters.in.Usuario;
import MeiaLuaQuadrado.ventz.adapters.repositories.IngressoRepository;
import MeiaLuaQuadrado.ventz.adapters.repositories.UsuarioRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/ingressos")
public class IngressoController {

    @Autowired
    IngressoRepository ingressoRepository;

    @Autowired
    ObjectMapper objectMapper;


//    @GetMapping("/buscarTodos")
//    public String getIngressos()  throws JsonProcessingException {
//        List<Ingresso> ingressos = ingressoRepository.findAll();
//
//        return objectMapper.writeValueAsString(ingressos);
//    }

    // http://localhost:8080/ingressos/buscarTodos
    @GetMapping("/buscarTodos")
    public ResponseEntity<String> getIngressos() {
        List<Ingresso> ingressos = ingressoRepository.findAll();

        if (!ingressos.isEmpty()) {
            return ResponseEntity.ok().body(ingressos.toString()); // Retorna a lista de ingressos
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhum ingresso encontrado");
        }
    }


//    @PostMapping("/inserirIngresso")
//    public void inserirIngresso(@RequestBody Ingresso ingresso){
//
//
//            ingressoRepository.save(ingresso);
//    }

    @PostMapping("/inserirIngresso")
    public ResponseEntity<String> inserirIngresso(@RequestBody Ingresso ingresso) {
        try {
            // Salva o ingresso no repositório
            ingressoRepository.save(ingresso);
            return ResponseEntity.status(HttpStatus.CREATED).body("Ingresso cadastrado com sucesso!");
        } catch (Exception e) {
            // Se ocorrer algum erro, retorna um status 500 (erro interno do servidor)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao cadastrar ingresso: " + e.getMessage());
        }
    }


//    @GetMapping("/buscarPorUsuario/{idUsuario}")
//    public String getIngressosByUsuarioId(@PathVariable("idUsuario") Integer idUsuario) throws JsonProcessingException {
//        // Busca os ingressos pelo ID do usuário
//        List<Ingresso> ingressos = ingressoRepository.findByUsuarioId(idUsuario);
//
//        if (ingressos.isEmpty()) {
//            return "Ingresso não encontrado para esse usuario";
//        }
//
//        return objectMapper.writeValueAsString(ingressos);  // Retorna a lista de ingressos
//    }


    // Mostra todos ingressos de um usuário
    //http://localhost:8080/ingressos/buscarPorUsuario/1
    @GetMapping("/buscarPorUsuario/{idUsuario}")
    public ResponseEntity<String> getIngressosByUsuarioId(@PathVariable("idUsuario") Integer idUsuario) throws JsonProcessingException {
        // Busca os ingressos pelo ID do usuário
        List<Ingresso> ingressos = ingressoRepository.findByUsuarioId(idUsuario);

        if (ingressos.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ingressos não encontrados para esse usuário.");
        } else {
            // Retorna a lista de ingressos como JSON
            return ResponseEntity.ok().body(objectMapper.writeValueAsString(ingressos));
        }
    }

        //http://localhost:8080/ingressos/buscarPorId/1
        @GetMapping("/buscarPorId/{idIngresso}")
        public ResponseEntity<String> getIngressosByIdIngresso(@PathVariable("idIngresso") Integer idIngresso) throws JsonProcessingException {
            // Busca os ingressos pelo ID do usuário
            Ingresso ingressos = ingressoRepository.findByIdIngresso(idIngresso).orElse(null);

            try {

                if (ingressos == null) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ingresso não encontrado");
                } else {
                    return ResponseEntity.ok(objectMapper.writeValueAsString(ingressos));// Retorna a lista de ingressos
                }
            }catch (JsonProcessingException e) {
                // Em caso de erro na serialização, retornar status 500
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao processar JSON");
            }
        }

        // http://localhost:8080/ingressos/utilizarIngresso/1
        // Método para utilizar o ingresso
        @PutMapping("/utilizarIngresso/{idIngresso}")
        public ResponseEntity<String> utilizarIngresso(@PathVariable("idIngresso") Integer idIngresso) {
            // Busca o ingresso pelo ID
            Optional<Ingresso> ingressoOpt = ingressoRepository.findByIdIngresso(idIngresso);

            if (ingressoOpt.isPresent()) {
                Ingresso ingresso = ingressoOpt.get();

            // Verifica se o ingresso está disponível
            if (ingresso.getDisponivel()) {
                // Marca o ingresso como indisponível (utilizado)
                ingresso.setDisponivel(false);
                ingressoRepository.save(ingresso);

                return ResponseEntity.ok().body("Ingresso utilizado com sucesso.");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("O ingresso já foi utilizado.");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ingresso não encontrado.");
        }
    }
}
