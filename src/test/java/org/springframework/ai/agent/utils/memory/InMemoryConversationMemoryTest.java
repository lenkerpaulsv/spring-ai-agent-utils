package org.springframework.ai.agent.utils.memory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InMemoryConversationMemoryTest {

    private InMemoryConversationMemory memory;

    @BeforeEach
    void setUp() {
        memory = new InMemoryConversationMemory();
    }

    @Test
    void shouldAddAndRetrieveMessages() {
        memory.add(new UserMessage("Hello"));
        memory.add(new AssistantMessage("Hi there!"));

        List<Message> messages = memory.getMessages();
        assertThat(messages).hasSize(2);
        assertThat(messages.get(0).getText()).isEqualTo("Hello");
        assertThat(messages.get(1).getText()).isEqualTo("Hi there!");
    }

    @Test
    void shouldReturnLastNMessages() {
        memory.add(new UserMessage("msg1"));
        memory.add(new UserMessage("msg2"));
        memory.add(new UserMessage("msg3"));

        List<Message> recent = memory.getMessages(2);
        assertThat(recent).hasSize(2);
        assertThat(recent.get(0).getText()).isEqualTo("msg2");
        assertThat(recent.get(1).getText()).isEqualTo("msg3");
    }

    @Test
    void shouldClearMessages() {
        memory.add(new UserMessage("Hello"));
        memory.clear();

        assertThat(memory.getMessages()).isEmpty();
        assertThat(memory.size()).isZero();
    }

    @Test
    void shouldEvictOldestMessageWhenCapacityExceeded() {
        InMemoryConversationMemory boundedMemory = new InMemoryConversationMemory(2);
        boundedMemory.add(new UserMessage("first"));
        boundedMemory.add(new UserMessage("second"));
        boundedMemory.add(new UserMessage("third"));

        assertThat(boundedMemory.size()).isEqualTo(2);
        assertThat(boundedMemory.getMessages().get(0).getText()).isEqualTo("second");
    }

    @Test
    void shouldThrowExceptionForNullMessage() {
        assertThatThrownBy(() -> memory.add(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("message must not be null");
    }

    @Test
    void shouldThrowExceptionForInvalidMaxCapacity() {
        assertThatThrownBy(() -> new InMemoryConversationMemory(0))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
