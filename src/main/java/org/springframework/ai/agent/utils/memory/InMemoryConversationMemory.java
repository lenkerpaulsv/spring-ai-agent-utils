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
 *
 * <p>When a {@code maxCapacity} is set and the limit is reached, the oldest
 * message is evicted before adding the new one (FIFO eviction policy).
 *
 * <p>Default max capacity is {@link Integer#MAX_VALUE} (effectively unbounded).
 * For typical chat applications, a capacity of 100-200 messages is recommended
 * to avoid unbounded memory growth.
 */
public class InMemoryConversationMemory implements ConversationMemory {

    // Lowered from Integer.MAX_VALUE to a more sensible default to prevent
    // accidental unbounded memory growth in long-running conversations.
    private static final int DEFAULT_MAX_CAPACITY = 1000;

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
