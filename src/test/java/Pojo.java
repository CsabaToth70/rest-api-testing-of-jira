public class Pojo {
    String id;
    String key;
    String self;

    public Pojo() {
    }

    public Pojo(String id, String key, String self) {
        this.id = id;
        this.key = key;
        this.self = self;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSelf() {
        return self;
    }

    public void setSelf(String self) {
        this.self = self;
    }
}
