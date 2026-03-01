package org.example.service.impl;

import org.example.entity.User;
import org.example.repository.UserRepository;
import org.example.utils.PasswordGenerator;
import org.example.utils.UsernameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock UserRepository userRepository;
    @Mock UsernameGenerator usernameGenerator;
    @Mock PasswordGenerator passwordGenerator;

    @InjectMocks UserServiceImpl userService;

    @Test
    void createUser_success() {
        when(usernameGenerator.generateUsername(eq("John"), eq("Doe"), any())).thenReturn("John.Doe");
        when(passwordGenerator.generatePassword()).thenReturn("pass123456");
        when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        User user = userService.createUser("John", "Doe");

        assertThat(user.getUsername()).isEqualTo("John.Doe");
        assertThat(user.getPassword()).isEqualTo("pass123456");
        assertThat(user.getFirstName()).isEqualTo("John");
        assertThat(user.getLastName()).isEqualTo("Doe");
        assertThat(user.isActive()).isTrue();
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_blankFirstName_throwsException() {
        assertThatThrownBy(() -> userService.createUser("", "Doe"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("First name is required");
        verifyNoInteractions(userRepository);
    }

    @Test
    void createUser_nullFirstName_throwsException() {
        assertThatThrownBy(() -> userService.createUser(null, "Doe"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("First name is required");
    }

    @Test
    void createUser_blankLastName_throwsException() {
        assertThatThrownBy(() -> userService.createUser("John", ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Last name is required");
    }

    @Test
    void createUser_nullLastName_throwsException() {
        assertThatThrownBy(() -> userService.createUser("John", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Last name is required");
    }

    @Test
    void createUser_usernameGeneratorCalled() {
        when(usernameGenerator.generateUsername(eq("John"), eq("Doe"), any())).thenReturn("John.Doe1");
        when(passwordGenerator.generatePassword()).thenReturn("abc1234567");
        when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        User user = userService.createUser("John", "Doe");

        assertThat(user.getUsername()).isEqualTo("John.Doe1");
        verify(usernameGenerator).generateUsername(eq("John"), eq("Doe"), any());
    }
}