public class ValueExpirationPair<T> {

  private T value;
  private Long expiration;

  public ValueExpirationPair(T value, Long expiration) {
    this.value = value;
    this.expiration = expiration;
  }

  public T getValue() {
    return value;
  }

  public void setValue(T value) {
    this.value = value;
  }

  public Long getExpiration() {
    return expiration;
  }

  public void setExpiration(Long expiration) {
    this.expiration = expiration;
  }
}
