package org.springframework.ai.agent.utils.memory;

import org.springframework.ai.chat.messages.Message;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * In-memory implementation of {@link ConversationMemory}.
 * Stores conversation messages in a bounded or unbounded in-memory list.
 */
public class InMemoryConversationMemory implements ConversationMemory {

    private static final int DEFAULT_MAX_CAPACITY = Integer.MAX_VALUE;

    private final LinkedList<Message> messages = new LinkedList<>();
    private final int maxCapacity;

    public InMemoryConversationMemory() {
        this.maxCapacity = DEFAULT_MAX_CAPACITY;
    }

    public InMemoryConversationMemory(int maxCapacity) {
        Assert.isTrue(maxCapacity > 0, "maxCapacity must be greater than 0");
        this.maxCapacity = maxCapacity;
    }

    @Override
    public synchronized void add(Message message) {
        Assert.notNull(message, "message must not be null");
        if (messages.size() >= maxCapacity) {
            messages.removeFirst();
        }
        messages.add(message);
    }

    @Override
    public synchronized List<Message> getMessages() {
        return Collections.unmodifiableList(new ArrayList<>(messages));
    }

    @Override
    public synchronized List<Message> getMessages(int maxMessages) {
        Assert.isTrue(maxMessages > 0, "maxMessages must be greater than 0");
        int fromIndex = Math.max(0, messages.size() - maxMessages);
        return Collections.unmodifiableList(new ArrayList<>(messages.subList(fromIndex, messages.size())));
    }

    @Override
    public synchronized void clear() {
        messages.clear();
    }

    @Override
    public synchronized int size() {
        return messages.size();
    }
}
