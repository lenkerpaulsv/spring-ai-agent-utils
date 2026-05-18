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
 * <p>Default max capacity is {@value DEFAULT_MAX_CAPACITY} messages.
 * For typical chat applications, a capacity of 100-200 messages is recommended
 * to avoid unbounded memory growth.
 *
 * <p>Note: All public methods are synchronized for thread safety, making this
 * implementation safe for use in concurrent environments.
 */
public class InMemoryConversationMemory implements ConversationMemory {

    // Lowered from 500 to 100 - in my experience most conversational agents
    // rarely exceed this, and it keeps memory footprint predictable.
    // 500 was still too generous for the lightweight use cases I'm targeting.
    // Lowering further to 50 for my personal projects - I'm mostly building
    // short-lived demo bots where 100 is overkill.
    private static final int DEFAULT_MAX_CAPACITY = 50;

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
