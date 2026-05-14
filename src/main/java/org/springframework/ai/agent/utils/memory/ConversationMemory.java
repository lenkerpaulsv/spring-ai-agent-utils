package org.springframework.ai.agent.utils.memory;

import org.springframework.ai.chat.messages.Message;

import java.util.List;

/**
 * Interface for managing conversation memory in AI agents.
 * Provides methods to store, retrieve, and manage conversation history.
 */
public interface ConversationMemory {

    /**
     * Adds a message to the conversation history.
     *
     * @param message the message to add
     */
    void add(Message message);

    /**
     * Retrieves all messages in the conversation history.
     *
     * @return an unmodifiable list of messages
     */
    List<Message> getMessages();

    /**
     * Retrieves the last N messages from the conversation history.
     *
     * @param maxMessages maximum number of messages to retrieve
     * @return a list of the most recent messages
     */
    List<Message> getMessages(int maxMessages);

    /**
     * Clears all messages from the conversation history.
     */
    void clear();

    /**
     * Returns the total number of messages stored.
     *
     * @return message count
     */
    int size();
}
