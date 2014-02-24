package jec.CM12sekine.packetflower.fireworkelement;

public abstract class Firework {
	private String packetInfo ;
	
	
	public abstract void setStartAndEndPacketFlowerSystemTime(long startPacketFlowerSystemTime) ;
	
	public abstract long getStartPacketFlowerSystemTime();
	
	public abstract long getEndPacketFlowerSystemTime();
	
	protected abstract void initSeeds(long seed); 
		
	public abstract boolean isComplete();

	public abstract void drawFireworks(int textureId, int i); 
	
	public abstract boolean isAfterFirstDraw() ;

	public abstract long getSeed() ;
}
