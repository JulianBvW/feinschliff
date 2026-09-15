package io.github.julianbvw.feinschliff.core.hud;

/**
 * Everything the debug overlay shows, read off the game once per frame.
 *
 * <p>A plain carrier with public fields: the adapter is the only writer and
 * {@link DebugOverlay} the only reader, and keeping it dumb is what allows the
 * formatting to live in the version-independent core while the lookups stay in
 * the version-specific adapter.
 *
 * <p>Fields are left at their defaults when the game cannot answer -- for
 * example {@link #lightKnown} stays {@code false} while the player's chunk is
 * not loaded, because asking for light or blocks in an unloaded chunk would
 * make the game generate and then write it.
 */
public final class DebugSnapshot {

	/** Frames and chunk updates, e.g. {@code 60 fps, 0 chunk updates}. */
	public String fpsInfo = "";

	public String renderChunkInfo = "";
	public String renderEntityInfo = "";
	public String worldInfo = "";

	public boolean inWorld;

	public double x;
	public double y;
	public double z;

	/** Set while the free camera is out, when it is somewhere else than the player. */
	public boolean freecam;
	public double cameraX;
	public double cameraY;
	public double cameraZ;

	public int blockX;
	public int blockY;
	public int blockZ;

	public float yaw;
	public float pitch;

	public boolean lightKnown;
	public int lightEffective;
	public int lightSky;
	public int lightBlock;

	public long worldTicks;
	public long seed;

	public boolean targetIsBlock;
	public boolean targetIsEntity;
	public int targetBlockId;
	public int targetMetadata;
	public int targetX;
	public int targetY;
	public int targetZ;
	public int targetFace;

	public long usedMemory;
	public long totalMemory;
	public long maxMemory;
}
