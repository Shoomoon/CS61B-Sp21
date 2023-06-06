package byow.Core;

import byow.TileEngine.TETile;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Random;

public class Record implements Serializable {
    private TETile[][] world;
    private Random random;
    private Position avatar;
    private Position door;
    private List<Position> treasures;
    private List<Position> guardians;
    private List<List<Position>> guardiansChasePath;
    private List<Room> roomsList;
    private int lives;
    // Turn lights on or off, toggled with key 'H'
    private boolean avatarSightOnly;
    private boolean chasePathDisplay;
    public Record(TETile[][] world, Random random, Position avatar, Position door,
                  List<Position> treasures, List<Position> guardians, List<List<Position>> guardiansChasePath, List<Room> roomsList,
                  int lives, boolean avatarSightOnly, boolean chasePathDisplay) {
        this.world = world;
        this.random = random;
        this.avatar = avatar;
        this.door = door;
        this.treasures = treasures;
        this.guardians = guardians;
        this.guardiansChasePath = guardiansChasePath;
        this.roomsList = roomsList;
        this.lives = lives;
        this.avatarSightOnly = avatarSightOnly;
        this.chasePathDisplay = chasePathDisplay;
    }

    public TETile[][] getWorld() {
        return world;
    }

    public Random getRandom() {
        return random;
    }

    public Position getAvatar() {
        return avatar;
    }

    public Position getDoor() {
        return door;
    }

    public List<Position> getTreasures() {
        return treasures;
    }

    public List<Position> getGuardians() {
        return guardians;
    }
    public List<Room> getRoomsList() {
        return roomsList;
    }

    public int getLives() {
        return lives;
    }

    public boolean getAvatarSightOnly() {
        return avatarSightOnly;
    }

    public boolean getChasePathDisplay() {
        return chasePathDisplay;
    }
    public List<List<Position>> getGuardiansChasePath() {
        return guardiansChasePath;
    }
}
