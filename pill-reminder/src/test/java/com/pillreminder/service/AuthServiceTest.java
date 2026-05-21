package com.pillreminder.service;

import com.pillreminder.config.JwtUtil;
import com.pillreminder.dto.Dtos.*;
import com.pillreminder.entity.User;
import com.pillreminder.enums.Role;
import com.pillreminder.exception.EmailAlreadyExistsException;
import com.pillreminder.repository.*;
import com.pillreminder.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private RefreshTokenRepository refreshTokenRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtUtil jwtUtil;
    @Mock private AuthenticationManager authenticationManager;

    @InjectMocks private AuthServiceImpl authService;

    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setFullName("Test User");
    }

    @Test
    @DisplayName("Register - success")
    void register_success() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_pw");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            return u;
        });
        when(jwtUtil.generateToken(anyString())).thenReturn("access_token");
        when(refreshTokenRepository.findByUser(any())).thenReturn(Optional.empty());
        when(refreshTokenRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        AuthResponse response = authService.register(registerRequest);

        assertThat(response.getAccessToken()).isEqualTo("access_token");
        assertThat(response.getUser().getEmail()).isEqualTo("test@example.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Register - duplicate email throws exception")
    void register_duplicateEmail_throws() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessageContaining("test@example.com");
    }

    @Test
    @DisplayName("Login - success")
    void login_success() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        User user = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("encoded_pw")
                .fullName("Test User")
                .role(Role.ROLE_USER)
                .build();

        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken(anyString())).thenReturn("access_token");
        when(refreshTokenRepository.findByUser(any())).thenReturn(Optional.empty());
        when(refreshTokenRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        AuthResponse response = authService.login(loginRequest);

        assertThat(response.getAccessToken()).isEqualTo("access_token");
        assertThat(response.getUser().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("Login - bad credentials throws")
    void login_badCredentials_throws() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("wrong");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(BadCredentialsException.class);
    }
}
