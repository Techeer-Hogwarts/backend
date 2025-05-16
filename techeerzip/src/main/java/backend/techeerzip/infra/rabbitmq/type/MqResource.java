package backend.techeerzip.infra.rabbitmq.type;

public enum MqResource {
    CRAWL("crawl_queue", "crawl.exchange", "crawl.#", "RabbitMqService"),
    INDEX("index_queue", "index.exchange", "index.#", "IndexService");

    private final String queue;
    private final String exchange;
    private final String key;
    private final String context;

    MqResource(String queue, String exchange, String key, String context) {
        this.queue = queue;
        this.exchange = exchange;
        this.key = key;
        this.context = context;
    }

    public String exchange() {
        return exchange;
    }

    public String routingKey() {
        return key;
    }

    public String queue() {
        return queue;
    }

    public String context() {
        return context;
    }
}
