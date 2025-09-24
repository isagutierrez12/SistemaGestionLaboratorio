
import com.laboratorio.model.Rol;
import com.laboratorio.model.Usuario;
import com.laboratorio.repository.RolRepository;
import com.laboratorio.repository.UsuarioRepository;
import com.laboratorio.repository.UsuarioRolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final UsuarioRolRepository usuarioRolRepository;
    private final BCryptPasswordEncoder passwordEncoder;

   
    @Override
    public void run(String... args) throws Exception {
        // Verificar si existe el rol ADMIN
        Rol adminRol = rolRepository.findByNombre("ADMIN");
        if (adminRol == null) {
            adminRol = new Rol();
            adminRol.setNombre("ADMIN");
            adminRol.setDescripcion("Rol administrador");
            adminRol = rolRepository.save(adminRol);
        }

        // Verificar si existe un usuario admin
        Usuario admin = usuarioRepository.findByUsername("admin");
        if (admin == null) {
            admin = new Usuario();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("123456"));
            admin.setNombre("Admin");
            admin.setPrimerApellido("Sistema");
            admin.setActivo(true);
            admin.setFechaCreacion(new java.util.Date());
            admin.setRoles(java.util.List.of(adminRol));
            usuarioRepository.save(admin);
        }
    }
}
