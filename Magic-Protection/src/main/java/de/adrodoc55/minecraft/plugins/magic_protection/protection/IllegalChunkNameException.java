package de.adrodoc55.minecraft.plugins.magic_protection.protection;

public class IllegalChunkNameException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public IllegalChunkNameException(String chunkName, Throwable cause) {
    super(String.format("Der Chunk-Name '%s' ist ungültig", chunkName), cause);
  }
}
