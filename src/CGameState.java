import javalib.worldimages.*;   // images, like RectangleImage or OverlayImages
import javalib.impworld.*;      // the abstract World class and the big-bang library
// for imperative worlds
import java.awt.*;
import java.util.ArrayList;     // the arraylist library from java
// and predefined colors (Red, Green, Yellow, Blue, Black, White)

// represents a util class
class Util {
  // generates a centipede body given the length and the currSpeed of the centipede
  ArrayList<BodySeg> generateCentBody(int length, int speed) {
    ArrayList<BodySeg> body = new ArrayList<>();
    for (int index = 0; index < length; index += 1) {
      boolean head = index == length - 1;
      Posn pos = new Posn((index - length + 1) * ITile.WIDTH + ITile.WIDTH / 2,
          ITile.HEIGHT / 2);
      Posn vel = new Posn(speed, 0);
      BodySeg curr = new BodySeg(pos, vel, head, true, true,
          3 * ITile.HEIGHT / 2, 0);
      body.add(curr);
    }
    return body;
  }

  // constructs an arraylist that has the elements of the given arraylist from the start to the
  // end
  <T> ArrayList<T> getElementsBetween(ArrayList<T> src, int start, int end) {
    ArrayList<T> cpArr = new ArrayList<>();
    for (int index = start; index < end; index += 1) {
      cpArr.add(src.get(index));
    }
    return cpArr;
  }

  // takes in an element and generates an arraylist with solely that element
  <T> ArrayList<T> singletonList(T item) {
    ArrayList<T> list = new ArrayList<>();
    list.add(item);
    return list;
  }

  // generates an arraylist of grass tiles given the width and height of the board to represent
  // the board
  ArrayList<ITile> generateGrassBoard(int row, int col) {
    ArrayList<ITile> garden = new ArrayList<>();
    for (int index = 0; index < row; index += 1) {
      this.generateGrassRow(garden, index, col, row);
    }
    return garden;
  }

  // EFFECT: modifies the arraylist given to add the current row of grass tiles
  // generates an arraylist of col amount of grass tiles for a given row number
  void generateGrassRow(ArrayList<ITile> garden, int rowInd, int col, int row) {
    for (int index = 0; index < col; index += 1) {
      garden.add(new GrassTile(rowInd * ITile.WIDTH + ITile.WIDTH / 2,
          index * ITile.HEIGHT + ITile.HEIGHT / 2, row * ITile.WIDTH));
    }
  }

  // gives an arraylist that is a copy of the given one
  <T> ArrayList<T> copy(ArrayList<T> src) {
    ArrayList<T> copy = new ArrayList<>();
    for (T item : src) {
      copy.add(item);
    }
    return copy;
  }

  // EFFECT: modifies the first given arraylist to include all the items in the
  // second arraylist
  // in essence "appending" the lists together
  // appends the lists together
  <T> void append(ArrayList<T> src1, ArrayList<T> src2) {
    for (T item : src2) {
      src1.add(item);
    }
  }

  // ASSUMPTION: the second posn is meant to be a tile posn; otherwise this method would not make
  // sense
  // are the two posns in range of each other?
  boolean inRange(Posn pos, Posn tilePosn) {
    return Math.abs(tilePosn.x - pos.x) <= ITile.WIDTH / 2
        && Math.abs(tilePosn.y - pos.y) <= ITile.HEIGHT / 2;
  }

  // EFFECT: modifies the givengarden to change one of the tiles to a dandelion
  // sprouts a dandelion in the given posn; in effect, replacing one of the tiles in the garden
  // with a dandelion tile
  void sproutDandelion(Posn posHit, ArrayList<ITile> garden) {
    IsGrass isGrass = new IsGrass();
    for (int index = 0; index < garden.size(); index += 1) {
      ITile tile = garden.get(index);
      if (isGrass.apply(tile) && tile.samePos(posHit)) {
        garden.set(index, new GrassToDan().apply(tile));
      }
    }
  }

  // EFFECT: modifies the given garden to change multiple tiles to a dandelion
  // sprouts dandelions in the given positions; in effect, replacing multiple tiles in the garden
  // with dandelion tiles
  void sproutDanInPosns(ArrayList<Posn> hitbox, ArrayList<ITile> garden) {
    for (Posn hitboxSeg : hitbox) {
      this.sproutDandelion(hitboxSeg, garden);
    }
  }
}

// represents a tile and introduces the tile's height and width constants
interface ITile {
  int HEIGHT = 40; // the height of a tile cell in pixels

  int WIDTH = 40; // the width of a tile cell in pixels

  int FULL_HP = 3; // to represent the default health of a dandelion tile

  // draws this tile onto the world scene given
  void draw(WorldScene s);

  //are these the tile's coordinates?
  boolean samePos(Posn pos);

  // in effect "replaces" this tile with a new tile with the same position given the
  // mouse button name and the bottom column of the board
  ITile replaceTile(String bName, int botCol);

  // to return the result of applying the given visitor to this tile
  <R> R accept(ITileVisitor<R> visitor);

  // is this tile in range of the given posn?
  boolean inRange(Posn pos);

  // lowers the HP of this ITile if it has an HP unit
  void lowerHP();

  // is the HP of this ITile zero or less?
  boolean noHP();

  // recovers the HP of this ITile to full if this ITile has an HP unit
  void fullHP();

  // gets a list of Posns representing the hitbox of this ITile
  ArrayList<Posn> hitBox();
}

// implements ITile and introduces the row and col fields, which represent x and y indices
abstract class ATile implements ITile {
  int row; // to represent the row of this ATile in terms of the grid in pixels
  int col; // to represent the col of this ATile in terms of the grid in pixels
  int width; // to represent the width of the garden this ATile is in pixels

  // the constructor
  ATile(int row, int col, int width) {
    this.row = row;
    this.col = col;
    this.width = width;
  }

  // draws this ATile - to be implemented by classes that extend ATile
  public abstract void draw(WorldScene s);

  // is this ATile at the same position of the given Posn?
  public boolean samePos(Posn pos) {
    return this.row == pos.x && this.col == pos.y;
  }

  // in effect, this gives the "replacement" of this ATile with a new tile
  // with the same position given the mouse button name and the bottom column of the board
  public ITile replaceTile(String bName, int botCol) {
    if (bName.equals("LeftButton") && this.col != botCol) {
      return new GrassTile(this.row, this.col, this.width);
    } else {
      return this;
    }
  }

  // is this ATile in range of the given posn?
  public boolean inRange(Posn pos) {
    Util util = new Util();
    for (Posn hitBodySeg : this.hitBox()) {
      if (util.inRange(pos, hitBodySeg)) {
        return true;
      }
    }
    return false;
  }

  // to return the result of applying the given visitor to this ATile
  public abstract <R> R accept(ITileVisitor<R> visitor);

  // by default, an ATile does not have an HP unit, so this method does nothing
  public void lowerHP() {
    // does not do anything since this ATile has no HP
  }

  // by default, an ATile does not have an HP unit, so this method does nothing
  public void fullHP() {
    // does not do anything since this ATile has no HP
  }

  // by default, an ATile does not have an HP unit, so it does not make sense to have noHP, so
  // it just returns false
  public boolean noHP() {
    return false;
  }

  // by default, this ATile's hitbox is just the current tile; so the only thing added to the
  // hitbox of this ATile is this ATile's center (row, col)
  public ArrayList<Posn> hitBox() {
    ArrayList<Posn> hitBox = new ArrayList<>();
    hitBox.add(new Posn(this.row, this.col));
    return hitBox;
  }
}

// represents a tile with no obstacles on it
class GrassTile extends ATile {
  // the constructor
  GrassTile(int row, int col, int width) {
    super(row, col, width);
  }

  // draws a GrassTile, a solid green cube and a black outline, onto the given world scene
  public void draw(WorldScene s) {
    WorldImage outline = new RectangleImage(WIDTH, HEIGHT, OutlineMode.SOLID, Color.BLACK);
    WorldImage grass = new RectangleImage(WIDTH - 1,
        HEIGHT - 1, OutlineMode.SOLID, Color.GREEN);
    s.placeImageXY(outline, this.row, this.col);
    s.placeImageXY(grass, this.row, this.col);
  }

  // if this tile's indices match a given set of indices, returns a Dandelion
  // if left botton is clicked) or Pebble (if right botton is clicked) with those
  // indices
  public ITile replaceTile(String bName, int botCol) {
    if (bName.equals("LeftButton") && this.col != botCol) {
      return new DandelionTile(this.row, this.col, FULL_HP, this.width);
    } else if (bName.equals("RightButton") && this.col != botCol) {
      return new PebbleTile(this.row, this.col, this.width);
    }
    return this;
  }

  // to return the result of applying the given visitor to this GrassTile
  public <R> R accept(ITileVisitor<R> visitor) {
    return visitor.visitGrass(this);
  }
}

// represents a pebble tile
class PebbleTile extends ATile {
  // the constructor
  PebbleTile(int row, int col, int width) {
    super(row, col, width);
  }

  // draws a PebbleTile, a solid gray cube and a black outline, onto the given world scene
  public void draw(WorldScene s) {
    WorldImage outline = new RectangleImage(WIDTH, HEIGHT, OutlineMode.SOLID, Color.BLACK);
    WorldImage grass = new RectangleImage(WIDTH - 1,
        HEIGHT - 1, OutlineMode.SOLID, Color.GRAY);
    s.placeImageXY(outline, this.row, this.col);
    s.placeImageXY(grass, this.row, this.col);
  }

  @Override
  // to return the result of applying the given visitor to this PebbleTile
  public <R> R accept(ITileVisitor<R> visitor) {
    return visitor.visitPeb(this);
  }

  @Override
  // the hitbox of this PebbleTile is eight tiles around this pebble tile including it's tile
  // position
  public ArrayList<Posn> hitBox() {
    boolean leftEdge = ITile.WIDTH / 2 == this.row;
    boolean rightEdge = this.width - ITile.WIDTH / 2 == this.row;
    boolean topEdge = ITile.HEIGHT / 2 == this.col;
    // pebbles will never be in the bottom edge

    ArrayList<Posn> pebbleHitBox = new ArrayList<>();
    pebbleHitBox.add(new Posn(this.row, this.col));

    if (!leftEdge) {
      pebbleHitBox.add(new Posn(this.row - ITile.WIDTH, this.col));
    }
    if (!rightEdge) {
      pebbleHitBox.add(new Posn(this.row + ITile.WIDTH, this.col));
    }
    if (!topEdge) {
      pebbleHitBox.add(new Posn(this.row, this.col - ITile.WIDTH));
    }
    pebbleHitBox.add(new Posn(this.row, this.col + ITile.WIDTH));

    return pebbleHitBox;
  }
}

// represents a tile with a dandelion tile
class DandelionTile extends ATile {
  int hp;

  // the constructor
  DandelionTile(int row, int col, int hp, int width) {
    super(row, col, width);
    this.hp = hp;
  }

  @Override
  // draws a DandelionTile, with the color depending on the HP, onto the given world scene
  public void draw(WorldScene s) {
    Color color = new Color(255, 255, -(this.hp - FULL_HP) * 75);
    WorldImage outline = new RectangleImage(WIDTH, HEIGHT, OutlineMode.SOLID, Color.BLACK);
    WorldImage grass = new RectangleImage(WIDTH - 1,
        HEIGHT - 1, OutlineMode.SOLID, color);
    s.placeImageXY(outline, this.row, this.col);
    s.placeImageXY(grass, this.row, this.col);
  }

  @Override
  // to return the result of applying the given visitor to this DandelionTile
  public <R> R accept(ITileVisitor<R> visitor) {
    return visitor.visitDan(this);
  }

  // EFFECT: lowers the HP of this DandelionTile
  public void lowerHP() {
    this.hp -= 1;
  }

  @Override
  // EFFECT: turns the health back to full health
  public void fullHP() {
    this.hp = FULL_HP;
  }

  @Override
  // is the HP of this DandelionTile less than or equal to 0?
  public boolean noHP() {
    return this.hp <= 0;
  }
}

// represents a moving projectile in the centipede game
interface IProjectile {
  // draws this IProjectile onto the given world scene
  void draw(WorldScene s);

  // updates this IProjectile (with a new y position) after 1 tick;
  void move();

  // is this IProjectile off the screen?
  boolean offScreen();

  // is this IProjectile in the same tile as the given body seg?
  boolean hitBodySeg(BodySeg bodySeg);

  // can this IProjectile hit the given ITile?
  boolean hitTile(ITile tile);
}

// represents an abstract projectile in the centipede game
abstract class AProjectile implements IProjectile {
  int x; // represents the x position of the dart in pixels
  int y; // represents the y position of the dart in pixels
  int speed;

  // the constructor
  AProjectile(int x, int y, int speed) {
    this.x = x;
    this.y = y;
    this.speed = speed;
  }

  // draws this AProjectile onto the given world scene
  public abstract void draw(WorldScene s);

  // move this AProjectile down by its speed
  public void move() {
    this.y -= this.speed;
  }

  // is this AProjectile off the screen?
  public boolean offScreen() {
    return this.y <= 0;
  }

  // is this AProjectile in the same tile as the given body segment?
  public boolean hitBodySeg(BodySeg bodySeg) {
    return bodySeg.posnInRange(new Posn(this.x, this.y));
  }

  // can this AProjectile hit the given tile?
  public boolean hitTile(ITile tile) {
    return tile.inRange(new Posn(this.x, this.y));
  }
}

// represents a non-existing projectile in the game
abstract class ANoProjectile implements IProjectile {
  // draws this ANoProjectile onto the given world scene, which in essence does nothing
  public void draw(WorldScene s) {
    // does not draw anything since there is no projectile
  }

  // draws this ANoProjectile onto the given world scene, which in essence does nothing
  public void move() {
    // does not move anything since there is no projectile
  }

  // is this ANoProjectile off the screen? Yes, always
  public boolean offScreen() {
    return true;
  }

  // does this ANoProjectile have the given posn? Never.
  public boolean hitBodySeg(BodySeg bodySeg) {
    return false;
  }

  // can this ANoProjectile hit any dandelion tile given the garden? No.
  public boolean hitTile(ITile tile) {
    return false;
  }
}

// represents a water balloon in the game
interface IWaterBalloon extends IProjectile {
  // explodes this IWaterBalloon if it hits the centipede or a dandelion in the
  // list of centipedes and garden
  void explode(ArrayList<Centipede> cents, ArrayList<ITile> garden);

  // is this IWaterBallon in the hitbo (the cell itself and its adjacent 8 cells)
  // of the given body segment?
  boolean bodySegInHitbox(BodySeg bodySeg);
}

// represents a moving water balloon in the game
class WaterBalloon extends AProjectile implements IWaterBalloon {
  public WaterBalloon(int x, int y, int speed) {
    super(x, y, speed);
  }

  // EFFECT: modifies the given world scene to include this WaterBalloon
  // draws this WaterBalloon onto the given world scene
  public void draw(WorldScene s) {
    WorldImage waterBalloon = new EllipseImage(ITile.WIDTH / 2, ITile.HEIGHT, OutlineMode.SOLID,
        Color.BLUE);
    s.placeImageXY(waterBalloon, this.x, this.y);
  }

  // EFFECT: modifies the given list of centipedes and list of tiles if any of the body segment
  // or dandelions if the water balloon or its splash collides with them
  public void explode(ArrayList<Centipede> cents, ArrayList<ITile> garden) {
    IsDandelion isDandelion = new IsDandelion();
    for (int index = 0; index < garden.size(); index += 1) {
      ITile tile = garden.get(index);
      if (isDandelion.apply(tile) && this.tileInHitBox(tile)) {
        if (tile.inRange(new Posn(this.x, this.y))) {
          garden.set(index, new DanToPeb().apply(tile));
        } else {
          tile.fullHP();
        }
      }
    }
    ArrayList<Centipede> cpCent = new ArrayList<>();
    Util util = new Util();
    for (Centipede cent : cents) {
      if (cent.splashHit(this)) {
        util.sproutDanInPosns(cent.posnsHit(this), garden);
        util.append(cpCent, cent.splitWaterBalloon(this));
      } else {
        cpCent.add(cent);
      }
    }
    cents.clear();
    for (Centipede cent : cpCent) {
      cents.add(cent);
    }
  }

  // is this water balloon or its splash inside the given body segment?
  public boolean bodySegInHitbox(BodySeg bodySeg) {
    for (Posn p : this.hitBox()) {
      if (bodySeg.posnInRange(p)) {
        return true;
      }
    }
    return false;
  }

  //is this water balloon or its splash inside the given ITile?
  boolean tileInHitBox(ITile tile) {
    for (Posn p : this.hitBox()) {
      if (tile.inRange(p)) {
        return true;
      }
    }
    return false;
  }

  // generates a list of posns for the water balloon and its splashes
  ArrayList<Posn> hitBox() {
    ArrayList<Posn> hitBox = new ArrayList<>();
    Posn p = new Posn(this.x, this.y - ITile.WIDTH / 2);
    hitBox.add(p);
    hitBox.add(new Posn(p.x, p.y - ITile.HEIGHT));
    hitBox.add(new Posn(p.x, p.y + ITile.HEIGHT));
    hitBox.add(new Posn(p.x - ITile.WIDTH, p.y));
    hitBox.add(new Posn(p.x + ITile.WIDTH, p.y));
    hitBox.add(new Posn(p.x + ITile.WIDTH, p.y + ITile.HEIGHT));
    hitBox.add(new Posn(p.x - ITile.WIDTH, p.y - ITile.HEIGHT));
    hitBox.add(new Posn(p.x - ITile.WIDTH, p.y + ITile.HEIGHT));
    hitBox.add(new Posn(p.x + ITile.WIDTH, p.y - ITile.HEIGHT));
    return hitBox;
  }
}

// represents a non-existing water balloon
class NoWaterBalloon extends ANoProjectile implements IWaterBalloon {

  @Override
  // explodes this non-existing, which has no effect
  public void explode(ArrayList<Centipede> cents, ArrayList<ITile> garden) {
  }

  @Override
  //is this water balloon or its splash inside the given body segment? No
  public boolean bodySegInHitbox(BodySeg bodySeg) {
    return false;
  }
}

// represents a dart that can be fired in the centipede game
interface IDart extends IProjectile {
  // did this IDart miss anything on the board?
  boolean missed();
}

// represents a non-existent dart in the centipede game
class NoDart extends ANoProjectile implements IDart {

  // NoDart cannot miss since there isn't a dart
  public boolean missed() {
    return false;
  }
}

// represents a moving dart in the centipede game
class Dart extends AProjectile implements IDart {
  public Dart(int x, int y, int speed) {
    super(x, y, speed);
  }

  // EFFECT: modifies the given world scene to include this Dart
  // draws this Dart onto the given world scene
  public void draw(WorldScene s) {
    WorldImage dart = new CircleImage(5, OutlineMode.SOLID, Color.BLACK);
    s.placeImageXY(dart, this.x, this.y);
  }

  // is this dart off the board?
  public boolean missed() {
    return this.y <= 0;
  }
}

// represents the player in the centipede game
class Gnome {
  int x;
  int y;
  int speed;

  // the constructor
  Gnome(int x, int y, int speed) {
    this.x = x;
    this.y = y;
    this.speed = speed;
  }

  // EFFECT: changes the given world scene by adding this gnome onto it
  // draws this gnome onto the given world scene
  void draw(WorldScene s, int streak) {
    WorldImage player = new StarImage(ITile.WIDTH / 2 - 1, 8, 2, OutlineMode.SOLID, Color.ORANGE);
    WorldImage playerBallon = new CircleImage(ITile.WIDTH / 2 - 1, OutlineMode.SOLID, Color.BLUE);
    if (streak >= 3) {
      s.placeImageXY(playerBallon, this.x, this.y);
    }
    s.placeImageXY(player, this.x, this.y);
  }

  // EFFECT: modifies the position of the gnome, meaning it modifies its x and y fields
  // moves the gnome (towards the playerDirection specified by the key) by one unit
  // (currSpeed), the gnome stays if it tries to move off the edge of the screen.
  void moveCell(String key, int edge) {
    if (key.equals("left") && this.x - this.speed >= ITile.WIDTH / 2) {
      this.x = this.x - ITile.WIDTH;
    } else if (key.equals("right") && this.x + this.speed <= edge - ITile.WIDTH / 2) {
      this.x = this.x + ITile.WIDTH;
    }
  }

  // moves the gnome (towards the playerDirection specified by the key) by one unit (currSpeed).
  // the gnome stays if it tries to move off the edge of the screen.
  void move(String key, int rightEdge, int botEdge, ArrayList<ITile> garden) {
    int x_away = 0;
    int y_away = 0;
    int x_dir = 0;
    int y_dir = 0;

    if (key.equals("left") && this.x - this.speed >= ITile.WIDTH / 2) {
      x_away = -this.speed;
      x_dir = -1;
    } else if (key.equals("right") && this.x + this.speed <= rightEdge - ITile.WIDTH / 2) {
      x_away = this.speed;
      x_dir = 1;
    } else if (key.equals("up") &&
        this.y - this.speed >= botEdge - 2 * ITile.HEIGHT - ITile.HEIGHT / 2) {
      y_away = -this.speed;
      y_dir = -1;
    } else if (key.equals("down") && this.y + this.speed <= botEdge - ITile.HEIGHT / 2) {
      y_away = this.speed;
      y_dir = 1;
    }
    IsDandelion isDandelion = new IsDandelion();
    boolean isDanAhead = false;
    for (ITile tile : garden) {
      isDanAhead = isDanAhead || isDandelion.apply(tile) && this.intersect(tile, x_dir, y_dir);
    }

    if (!isDanAhead) {
      this.x += x_away;
      this.y += y_away;
    }
  }

  // will this player's model intersect with the given tile given the direction
  // the player is moving in?
  boolean intersect(ITile tile, int xDir, int yDir) {
    if (xDir == 0 && yDir == 0) {
      return false;
    } else if (xDir != 0) {
      int x_displace = this.x + xDir * ITile.WIDTH / 2;
      return tile.inRange(new Posn(x_displace, this.y))
          || tile.inRange(new Posn(x_displace, (this.y + 1) - ITile.HEIGHT / 2))
          || tile.inRange(new Posn(x_displace, (this.y - 1) + ITile.HEIGHT / 2));
    }
    int y_displace = this.y + yDir * ITile.HEIGHT / 2;
    return tile.inRange(new Posn(this.x, y_displace))
        || tile.inRange(new Posn((this.x + 1) - ITile.WIDTH / 2, y_displace))
        || tile.inRange(new Posn((this.x - 1) + ITile.WIDTH / 2, y_displace));
  }

  // generates an dart from the center of the tile where is gnome is at
  IDart generateDart() {
    return new Dart((this.x / ITile.WIDTH) * ITile.WIDTH + ITile.WIDTH / 2, this.y,
        ITile.HEIGHT / 2);
  }

  // generates an water balloon from the center of the tile where is gnome is at
  IWaterBalloon generateWaterBallon() {
    return new WaterBalloon((this.x / ITile.WIDTH) * ITile.WIDTH + ITile.WIDTH / 2, this.y,
        ITile.HEIGHT / 2);
  }

  // is this gnome inside the same tile as the given posn?
  boolean inRange(Posn pos) {
    return new Util().inRange(new Posn(this.x, this.y), pos);
  }
}

// represents a centipede in the centipede game
class Centipede {
  ArrayList<BodySeg> body; // represents all the body segments of this centipede
  // NOTE: the centipede's head is at the end of the list
  int maxSpeed; // the maximum speed the centipede can go in pixels
  int currSpeed; // how fast the centipede is currently moving in pixels
  ArrayList<ObstacleList> encountered; // represents all the dandelions this centipede has
  // encountered for every direction it has been in
  ArrayList<ITile> pebsAlreadyOn; // represents all the pebbles this centipede has encountered

  // the constructor
  Centipede(ArrayList<BodySeg> body, int maxSpeed, int currSpeed,
      ArrayList<ObstacleList> encountered, ArrayList<ITile> pebsAlreadyOn) {
    if (body.size() == 0) {
      throw new IllegalArgumentException("Centipede cannot have an empty body");
    }
    if (maxSpeed > ITile.WIDTH || currSpeed > ITile.WIDTH) {
      throw new IllegalArgumentException("Speed cannot shoot over a tile.");
    }
    this.body = body;
    this.maxSpeed = maxSpeed;
    this.currSpeed = currSpeed;
    this.encountered = encountered;
    this.pebsAlreadyOn = pebsAlreadyOn;
  }

  // the default constructor - constructs the starting centipede in the centipede
  // game
  Centipede(int length, int speed) {
    this(new Util().generateCentBody(length, speed), speed, speed,
        new Util().singletonList(new ObstacleList(0)), new ArrayList<>());
  }

  // EFFECT: changes the given world scene by adding this centipede onto it
  // draws this centipede onto the given world scene
  void draw(WorldScene s) {
    for (BodySeg bodyPart : this.body) {
      bodyPart.draw(s);
    }
  }

  // did the given dart hit any part of this centipede?
  boolean dartHit(IDart dart) {
    for (BodySeg bodySeg : this.body) {
      if (dart.hitBodySeg(bodySeg)) {
        return true;
      }
    }
    return false;
  }

  // does this centipede touch the given gnome?
  boolean hitPlayer(Gnome gnome) {
    for (BodySeg bodySeg : this.body) {
      if (bodySeg.gnomeInRange(gnome)) {
        return true;
      }
    }
    return false;
  }

  // did the given water balloon hit any part of this centipede?
  boolean waterBalloonHit(IWaterBalloon waterBalloon) {
    for (BodySeg bodySeg : this.body) {
      if (waterBalloon.hitBodySeg(bodySeg)) {
        return true;
      }
    }
    return false;
  }

  // does the given water balloon (or its splash) hit this centipede?
  boolean splashHit(IWaterBalloon waterBalloon) {
    for (BodySeg bodySeg : this.body) {
      if (waterBalloon.bodySegInHitbox(bodySeg)) {
        return true;
      }
    }
    return false;
  }

  // EFFECT: adds a pebble tile to this centipede's encountered pebble list to
  // prevent double
  // counting
  // is this centipede on a pebble it wasn't on before?
  boolean onPebble(ArrayList<ITile> garden) {
    IsPebble isPebble = new IsPebble();
    for (ITile tile : garden) {
      if (isPebble.apply(tile) && !this.inPebsAlreadyOn(tile) && this.anyInRange(tile)) {
        this.pebsAlreadyOn.add(tile);
        return true;
      }
    }
    return false;
  }

  // is this given tile one of this centipede's already encountered pebble list?
  boolean inPebsAlreadyOn(ITile tile) {
    for (ITile pebble : this.pebsAlreadyOn) {
      if (pebble == tile) {
        return true;
      }
    }
    return false;
  }

  // is any of this centipede's body segment in range of the given tile?
  boolean anyInRange(ITile tile) {
    for (BodySeg bodySeg : this.body) {
      if (bodySeg.tileInRange(tile)) {
        return true;
      }
    }
    return false;
  }

  // gets the position of where the dart hit this centipede
  Posn positionHit(IDart dart) {
    int indexHit = this.getIndexHit(dart);
    return this.body.get(indexHit).spawnTilePosn();
  }

  // splits this centipede into multiple centipedes depending on where the dart
  // hit this
  // centipede
  ArrayList<Centipede> splitDart(IDart dart) {
    ArrayList<Centipede> centipedes = new ArrayList<>();
    int indexHit = this.getIndexHit(dart);
    ArrayList<BodySeg> frontBody = new Util().getElementsBetween(this.body, 0, indexHit);
    if (frontBody.size() > 0) {
      centipedes.add(this.makeCentipede(frontBody));
    }
    if (indexHit + 1 < this.body.size()) {
      ArrayList<BodySeg> backBody = new Util().getElementsBetween(this.body, indexHit + 1,
          this.body.size());
      if (backBody.size() > 0) {
        centipedes.add(this.makeCentipede(backBody));
      }
    }
    return centipedes;
  }

  // splits this centipede into multiple centipedes depending on where the water
  // balloon hit this
  // centipede
  ArrayList<Centipede> splitWaterBalloon(IWaterBalloon waterBalloon) {
    ArrayList<Integer> indicesHit = this.getIndicesHit(waterBalloon);
    ArrayList<Centipede> centipedes = new ArrayList<>();
    Util util = new Util();
    int startInd = 0;
    for (int index : indicesHit) {
      ArrayList<BodySeg> bodySegs = util.getElementsBetween(this.body, startInd, index);
      if (bodySegs.size() > 0) {
        centipedes.add(makeCentipede(bodySegs));
      }
      startInd = index + 1;
    }

    if (startInd < this.body.size()) {
      ArrayList<BodySeg> bodySegs = util.getElementsBetween(this.body, startInd, this.body.size());
      centipedes.add(makeCentipede(bodySegs));
    }
    return centipedes;
  }

  // ASSUMPTION: the given ArrayList<BodySeg> cannot be empty; otherwise there
  // will be an
  // index out of bounds
  // makes a centipede with the same fields as this one but instead with a
  // different given
  // body
  Centipede makeCentipede(ArrayList<BodySeg> bodySegs) {
    bodySegs.get(bodySegs.size() - 1).toHead();
    for (BodySeg bodySeg : bodySegs) {
      bodySeg.setSpeed(this.maxSpeed);
    }
    return new Centipede(bodySegs, this.maxSpeed, this.maxSpeed, this.copyEncountered(),
        new ArrayList<>());
  }

  // copies this list of ObstacleLists to another Array
  ArrayList<ObstacleList> copyEncountered() {
    ArrayList<ObstacleList> cpEncountered = new ArrayList<>();
    for (ObstacleList obl : this.encountered) {
      cpEncountered.add(new ObstacleList(obl));
    }
    return cpEncountered;
  }

  // gets the positions of where the water balloon hit this centipede
  ArrayList<Integer> getIndicesHit(IWaterBalloon waterBalloon) {
    ArrayList<Integer> indicesHit = new ArrayList<>();
    for (int index = 0; index < this.body.size(); index += 1) {
      if (waterBalloon.bodySegInHitbox(this.body.get(index))) {
        indicesHit.add(index);
      }
    }
    return indicesHit;
  }

  // gets the index of the body segment
  int getIndexHit(IDart dart) {
    for (int index = 0; index < this.body.size(); index += 1) {
      if (dart.hitBodySeg(this.body.get(index))) {
        return index;
      }
    }
    throw new RuntimeException("The dart did not hit any of the body segments.");
  }

  // gets the position of where the water balloon hit this centipede
  ArrayList<Posn> posnsHit(IWaterBalloon balloon) {
    ArrayList<Integer> indicesHit = this.getIndicesHit(balloon);
    ArrayList<Posn> posns = new ArrayList<>();
    for (int index : indicesHit) {
      posns.add(this.body.get(index).tilePosn());
    }
    return posns;
  }

  // EFFECT: changes the all the elements in this centipede's list of body
  // positions,
  // essentially moving it along in the world
  // moves the centipede along the board in the world
  void move(int width, int height, ArrayList<ITile> garden) {
    if (this.onPebble(garden)) {
      this.halveSpeed();
    }
    BodySeg head = this.body.get(this.body.size() - 1);
    if (head.reverseYDirection(height)) {
      this.encountered.add(head.generateObstacleList());
    }
    ObstacleList headObl = head.obstacleList(this.encountered);
    if (head.aheadDandelion(garden) && !head.trapped(width, headObl)) {
      headObl.addToObstacles(head.nextTilePosn());
    }
    for (BodySeg bodySeg : this.body) {
      bodySeg.reverseYDirection(height);
      bodySeg.move(width, this.currSpeed, bodySeg.obstacleList(this.encountered));
    }
    this.removeUnusedObl();
    this.removeUnusedPeb();
  }

  // removes any pebbles that was not stepped on
  void removeUnusedPeb() {
    ArrayList<ITile> usedPebs = new ArrayList<>();
    for (ITile tile : this.pebsAlreadyOn) {
      if (this.anyInRange(tile)) {
        usedPebs.add(tile);
      }
      else {
        this.doubleSpeed();
      }
    }
    this.pebsAlreadyOn.clear();
    for (ITile tile : usedPebs) {
      this.pebsAlreadyOn.add(tile);
    }
  }

  // EFFECT: doubles this centipede's speed if it does not reach the maximum
  void doubleSpeed() {
    int currSpeed = this.maxSpeed;
    while (currSpeed / 2 > this.currSpeed) {
      currSpeed /= 2;
    }
    this.currSpeed = currSpeed;

    for (BodySeg bodySeg : this.body) {
      bodySeg.setSpeed(this.currSpeed);
    }
  }

  // EFFECT: halves this centipede's speed if it is greater than 1
  void halveSpeed() {
    if (this.currSpeed > 1) {
      this.currSpeed /= 2;
    }
    for (BodySeg bodySeg : this.body) {
      bodySeg.setSpeed(this.currSpeed);
    }
  }

  // EFFECT: modifies the centipede's encountered to remove any ObstacleList that
  // have not
  // been used
  // removes any unused obstacle lists from this centipede's encountered list
  void removeUnusedObl() {
    ArrayList<ObstacleList> used = new ArrayList<>();
    for (ObstacleList obl : this.encountered) {
      if (this.usedObl(obl)) {
        used.add(obl);
      }
    }
    this.encountered.clear();
    for (ObstacleList obl : used) {
      this.encountered.add(obl);
    }
  }

  // has the given obstacle list been used by any of the body segments?
  boolean usedObl(ObstacleList obl) {
    for (BodySeg bodySeg : this.body) {
      if (bodySeg.sameOblIteration(obl)) {
        return true;
      }
    }
    return false;
  }
}

//represents a body segment of a centipede
class BodySeg {
  Posn pos; // represents the position of this body segment
  Posn velocity; // represents the velocity of this body segment
  boolean head; // is this body segment the head?
  boolean down; // is this body segment going down?
  boolean right; // is this body segment going right?
  int nextRow;
  int iteration; // represents the number of times this body segment has bounced
  // (switched directions)

  // the constructor
  BodySeg(Posn pos, Posn velocity, boolean head, boolean down, boolean right, int nextRow,
      int iteration) {
    this.pos = pos;
    this.velocity = velocity;
    this.head = head;
    this.down = down;
    this.right = right;
    this.nextRow = nextRow;
    this.iteration = iteration;
  }

  // EFFECT: changes the position and velocity of this body segment
  // moves this body segment
  void move(int width, int speed, ObstacleList obl) {
    boolean inNextRow = Math.abs(this.pos.y - nextRow) <= speed / 2;

    if (this.obstacleAhead(speed, width, obl)) {
      int middle_x = (this.pos.x / ITile.WIDTH) * ITile.WIDTH + ITile.WIDTH / 2;
      int excessSpeed = Math.abs(this.pos.x + this.velocity.x - middle_x);
      if (this.velocity.x == 0) {
        if (this.down) {
          this.nextRow += ITile.HEIGHT;
        }
        else {
          this.nextRow -= ITile.HEIGHT;
        }
        excessSpeed = Math.abs(this.velocity.y);
      }

      if (this.down) {
        this.pos = new Posn(middle_x, this.pos.y + excessSpeed);
        this.velocity = new Posn(0, speed);
      }
      else {
        this.pos = new Posn(middle_x, this.pos.y - excessSpeed);
        this.velocity = new Posn(0, -speed);
      }

      this.right = !this.right;

    }
    else if (inNextRow && this.velocity.x == 0) {
      if (this.down) {
        this.nextRow += ITile.HEIGHT;
      }
      else {
        this.nextRow -= ITile.HEIGHT;
      }
      int middle_y = (this.pos.y / ITile.HEIGHT) * ITile.HEIGHT + ITile.HEIGHT / 2;
      int excessSpeed = Math.abs(this.pos.y + this.velocity.y - middle_y);
      if (this.right) {
        this.pos = new Posn(this.pos.x + excessSpeed, middle_y);
        this.velocity = new Posn(speed, 0);
      }
      else {
        this.pos = new Posn(this.pos.x - excessSpeed, middle_y);
        this.velocity = new Posn(-speed, 0);
      }
    }
    else {
      this.pos = new Posn(this.pos.x + this.velocity.x, this.pos.y + this.velocity.y);
    }
  }

  // is there a dandelion ahead of this centipede?
  boolean obstacleAhead(int speed, int width, ObstacleList obl) {
    boolean leftEdge = Math.abs(this.pos.x - ITile.WIDTH / 2) <= speed / 2;
    boolean rightEdge = Math.abs(this.pos.x - (width - ITile.WIDTH / 2)) <= speed / 2;
    boolean inRow = (this.pos.y - ITile.HEIGHT / 2) % ITile.HEIGHT <= speed / 2
        || (this.pos.y - ITile.HEIGHT / 2) % ITile.HEIGHT >= ITile.HEIGHT - speed / 2;

    return this.nextEncountered(obl) && inRow || leftEdge && inRow && !this.right
        || rightEdge && inRow && this.right;
  }

  // EFFECT: changes the given world scene by adding this body segment onto it
  // draws this body segment onto the given world scene
  void draw(WorldScene s) {
    Color color = Color.BLUE;
    if (this.head) {
      color = Color.CYAN;
    }

    WorldImage bodyPart = new CircleImage(ITile.WIDTH / 2 - 1, OutlineMode.SOLID, color);
    s.placeImageXY(bodyPart, this.pos.x, this.pos.y);
  }

  // EFFECT: changes the value of whether or not this body segment is a head
  // (changes the head boolean)
  // turns this body segment into a head
  void toHead() {
    this.head = true;
  }

  // offsets both the vertical and horizontal velocity's to the given speed
  void setSpeed(int speed) {
    int vel_x = this.velocity.x;
    int vel_y = this.velocity.y;
    if (vel_x != 0) {
      vel_x = Math.abs(vel_x) / vel_x * speed;
    }
    if (vel_y != 0) {
      vel_y = Math.abs(vel_y) / vel_y * speed;
    }
    this.velocity = new Posn(vel_x, vel_y);
  }

  // determines if the given obstacle list has the same iteration as this body
  // segment
  boolean sameOblIteration(ObstacleList obl) {
    return obl.sameIteration(this.iteration);
  }

  // is this body segment in range of the given posn?
  boolean posnInRange(Posn p) {
    return new Util().inRange(this.pos, p);
  }

  // is this body segment inside the given tile?
  boolean tileInRange(ITile tile) {
    return tile.inRange(this.pos);
  }

  // does this body segment touch the given gnome?
  boolean gnomeInRange(Gnome gnome) {
    return gnome.inRange(this.pos);
  }

  // gives the obstacle list that has the same iteration as this body segment
  ObstacleList obstacleList(ArrayList<ObstacleList> encountered) {
    for (ObstacleList obl : encountered) {
      if (obl.sameIteration(this.iteration)) {
        return obl;
      }
    }
    throw new RuntimeException("No such Obstacle List found");
  }

  // generates a new obstacle list with this body segment's iteration
  ObstacleList generateObstacleList() {
    return new ObstacleList(this.iteration);
  }

  // EFFECT: reverses the direction of this body segment potentially, also
  // increments
  // the iteration by one since it has just "bounced"
  // returns true if successful, false if otherwise
  boolean reverseYDirection(int height) {
    boolean topRow = this.pos.y / ITile.HEIGHT * ITile.HEIGHT + ITile.HEIGHT / 2 == ITile.HEIGHT
        / 2;
    boolean botRow = this.pos.y / ITile.HEIGHT * ITile.HEIGHT + ITile.HEIGHT / 2 == height
        - ITile.HEIGHT / 2;

    if (this.down && botRow || !this.down && topRow) {
      this.iteration += 1;
      this.down = !this.down;
      return true;
    }
    return false;
  }

  // is there a position to the right or left of this body segment (depending on
  // direction)
  // where it will collide in the given list?
  boolean nextEncountered(ObstacleList obl) {
    Posn pos = this.nextTilePosn();
    return obl.inObstacles(pos);
  }

  // gets the tile position of this body segment to spawn
  Posn spawnTilePosn() {
    int x = (this.pos.x / ITile.WIDTH) * ITile.WIDTH + ITile.WIDTH / 2;
    int y = (this.pos.y / ITile.HEIGHT) * ITile.HEIGHT + ITile.HEIGHT / 2;
    if (this.right && this.pos.x % ITile.WIDTH > ITile.WIDTH / 2) {
      x += ITile.WIDTH;
    }
    else if (!this.right && this.pos.x % ITile.WIDTH < ITile.WIDTH / 2) {
      x -= ITile.WIDTH;
    }
    return new Posn(x, y);
  }

  // gets the tile posn this BodySeg is currently on
  Posn tilePosn() {
    return new Posn((this.pos.x / ITile.WIDTH) * ITile.WIDTH + ITile.WIDTH / 2,
        (this.pos.y / ITile.HEIGHT) * ITile.HEIGHT + ITile.HEIGHT / 2);
  }

  // returns the center of this tile if it is nearing a tile (meaning it is equal
  // to
  // or greater than the middle (or less if going left), otherwise it just returns
  // the current posn
  Posn centeredGreater() {
    if (this.right && this.pos.x % ITile.WIDTH >= ITile.WIDTH / 2
        || !this.right && this.pos.x % ITile.WIDTH <= ITile.WIDTH / 2) {
      return new Posn(this.pos.x / ITile.WIDTH * ITile.WIDTH + ITile.WIDTH / 2, this.pos.y);
    }
    return pos;
  }

  // gives the next position (depending on direction) of this body segment
  // NOTE: this will give an invalid position if the centipede is at one of the
  // edges in which
  // the body segment maintains its direction towards that edge
  Posn nextTilePosn() {
    Posn centered = this.centeredGreater();
    Posn ahead = new Posn(centered.x + ITile.WIDTH, centered.y);
    if (!this.right) {
      ahead = new Posn(centered.x - ITile.WIDTH, centered.y);
    }
    return ahead;
  }

  // gives the previous position (depending on direction) of this body segment
  // NOTE: this will give an invalid position if the centipede is at one of the
  // edges in which
  // the body segment maintains its opposite direction towards that edge
  Posn prevTilePosn() {
    Posn centered = this.centeredGreater();
    Posn behind = new Posn(centered.x - ITile.WIDTH, centered.y);
    if (!this.right) {
      behind = new Posn(centered.x + ITile.WIDTH, centered.y);
    }
    return behind;
  }

  // is there a dandelion ahead of this body segment?
  boolean aheadDandelion(ArrayList<ITile> garden) {
    Posn ahead = this.nextTilePosn();
    IsDandelion isDandelion = new IsDandelion();
    for (ITile tile : garden) {
      if (isDandelion.apply(tile) && tile.samePos(ahead)) {
        return true;
      }
    }
    return false;
  }

  // can this BodySeg be trapped by the board?
  boolean trapped(int width, ObstacleList obl) {
    Posn ahead = this.nextTilePosn();
    Posn ahead_away2y = new Posn(ahead.x, ahead.y - 2 * ITile.HEIGHT);
    Posn prev = this.prevTilePosn();
    Posn prev_away1y = new Posn(prev.x, prev.y - ITile.HEIGHT);
    if (!this.down) {
      ahead_away2y = new Posn(ahead.x, ahead.y + 2 * ITile.HEIGHT);
      prev_away1y = new Posn(ahead.x, ahead.y + ITile.HEIGHT);
    }

    boolean obstacleTwoYNext = obl.inObstacles(ahead_away2y);
    boolean obstacleOneYPrev = obl.inObstacles(prev_away1y) || prev.x < 0 || prev.x > width;

    return obstacleTwoYNext && obstacleOneYPrev;
  }
}

// represents a list of all obstacles encountered during a certain period, or iteration, when
// the centipede was/is moving in
class ObstacleList {
  int iteration; // represents how many times the centipede has bounced
  ArrayList<Posn> obstacles; // all the obstacles encountered during this iteration

  ObstacleList(int iteration, ArrayList<Posn> obstacles) {
    this.iteration = iteration;
    this.obstacles = obstacles;
  }

  // the default constructor - constructs a new obstacle list with a new iteration
  // with no
  // obstacles encountered
  ObstacleList(int iteration) {
    this(iteration, new ArrayList<>());
  }

  // constructs a copy of the given ObstacleList
  ObstacleList(ObstacleList other) {
    this(other.iteration, new Util().copy(other.obstacles));
  }

  // is this iteration the same as the one given?
  boolean sameIteration(int iteration) {
    return this.iteration == iteration;
  }

  // EFFECT: modifies this ObstacleList's obstacles by adding a new obstacle/posn
  // to it
  // adds a new obstacle to this list of obstacles
  void addToObstacles(Posn p) {
    this.obstacles.add(p);
  }

  // is the given posn in this list of obstacles?
  boolean inObstacles(Posn p) {
    for (Posn obstacle : this.obstacles) {
      if (p.equals(obstacle)) {
        return true;
      }
    }
    return false;
  }
}

// represents the actual game world when the player can control the gnome
class CGameState extends GameState {
  ArrayList<Centipede> cents; // represents all the centipedes in the current world
  int length;
  int speed;
  ArrayList<ITile> garden; // represents all the tiles in the current world
  IDart dart;
  IWaterBalloon waterBalloon;
  Gnome gnome;
  Posn playerDirection; // -1 if player is moving left, 0 is player is not moving,
  // and 1 if player is moving right for the x component,
  // and the same respectively for moving down and up
  int score;
  int streak;
  int width;
  int height;

  // the default constructor, only requiring how big the board should be
  CGameState(int x, int y, ArrayList<ITile> garden, Gnome gnome) {
    this(new Util().singletonList(new Centipede(10, 4)), 10, 4, garden, new Posn(0, 0), gnome,
        new NoDart(), new NoWaterBalloon(), 0, 0, ITile.WIDTH * x, ITile.HEIGHT * y);
  }

  // the constructor
  CGameState(ArrayList<Centipede> cents, int length, int speed, ArrayList<ITile> garden,
      Posn playerDirection, Gnome gnome, IDart dart, IWaterBalloon waterBalloon, int score,
      int streak, int width, int height) {
    if (width < 2 * ITile.WIDTH || height < 2 * ITile.HEIGHT) {
      throw new IllegalArgumentException("Invalid dimensions");
    }
    this.cents = cents;
    this.length = length;
    this.speed = speed;
    this.garden = garden;
    this.playerDirection = playerDirection;
    this.gnome = gnome;
    this.dart = dart;
    this.waterBalloon = waterBalloon;
    this.score = score;
    this.streak = streak;
    this.width = width;
    this.height = height;
  }

  @Override
  // EFFECT: modifies the centipedes, the garden, the player, dart, water balloon,
  // score,
  // and streak, after they interact with each other after each tick.
  public void onTick() {
    if (this.cents.size() == 0) {
      this.length += 1;
      this.speed += 2;
      this.cents.add(new Centipede(this.length, this.speed));
    }
    this.collidesDandelion();
    this.collidesCentipede();
    this.collidesWaterBalloon();

    for (Centipede c : this.cents) {
      c.move(this.width, this.height, this.garden);
    }

    this.movePlayer();
    this.moveDart();
    this.moveWaterBalloon();
  }

  // is the game over? (did the centipede touch the gnome?)
  public boolean endGame() {
    for (Centipede cent : this.cents) {
      if (cent.hitPlayer(this.gnome)) {
        return true;
      }
    }
    return false;
  }

  // generates a worldscene that displays the game over message and
  // the final score when the game ends
  public WorldScene lastScene(String s) {
    WorldScene worldScene = new WorldScene(this.width, this.height);
    WorldImage gg = new TextImage("Game Over", Color.BLACK);
    WorldImage score = new TextImage("" + this.score, Color.BLACK);
    worldScene.placeImageXY(gg, width / 2, height / 2 - 20);
    worldScene.placeImageXY(score, width / 2, height / 2);
    return worldScene;
  }

  // EFFECT: modifies the player position of this CGameState based on the player
  // direction
  // moves the player accordingly based on the key input the user gave
  void movePlayer() {
    if (this.playerDirection.x == 1) {
      this.gnome.move("right", this.width, this.height, this.garden);
    }
    if (this.playerDirection.x == -1) {
      this.gnome.move("left", this.width, this.height, this.garden);
    }

    if (this.playerDirection.y == 1) {
      this.gnome.move("up", this.width, this.height, this.garden);
    }
    if (this.playerDirection.y == -1) {
      this.gnome.move("down", this.width, this.height, this.garden);
    }
  }

  // EFFECT: modifies the IDart and score of this CGameState, either directly
  // modifying the IDart
  // or setting it equal to a different IDart, and modifying the score when needed
  // moves the Dart in the game
  void moveDart() {
    if (this.dart.missed()) {
      this.score -= 1;
      this.streak = 0;
    }

    if (this.dart.offScreen()) {
      this.dart = new NoDart();
    }
    else {
      this.dart.move();
    }
  }

  // EFFECT: modifies the IDart and score of this CGameState, either directly
  // modifying the IDart
  // or setting it equal to a different IDart, and modifying the score when needed
  // moves the Dart in the game
  void moveWaterBalloon() {
    if (this.waterBalloon.offScreen()) {
      this.waterBalloon = new NoWaterBalloon();
    }
    else {
      this.waterBalloon.move();
    }
  }

  // EFFECT: updates the water balloon and the garden after they interact with
  // each other
  void collidesWaterBalloon() {
    IsDandelion isDandelion = new IsDandelion();
    for (int index = 0; index < this.garden.size(); index += 1) {
      ITile tile = this.garden.get(index);
      if (isDandelion.apply(tile) && this.waterBalloon.hitTile(tile)) {
        this.score += this.numberBodySegHit() * 10;
        waterBalloon.explode(this.cents, this.garden);
        this.waterBalloon = new NoWaterBalloon();
      }
    }
    if (this.hitCentipede()) {
      this.score += this.numberBodySegHit() * 10;
      waterBalloon.explode(this.cents, this.garden);
      this.waterBalloon = new NoWaterBalloon();
    }
  }

  // counts the number of body segments that is hit by the splashes of the water
  // balloon
  int numberBodySegHit() {
    int ctr = 0;
    for (Centipede cent : this.cents) {
      if (cent.splashHit(this.waterBalloon)) {
        ctr += cent.getIndicesHit(this.waterBalloon).size();
      }
    }
    return ctr;
  }

  // did any of the centipedes get hit by the water balloon?
  boolean hitCentipede() {
    for (Centipede cent : this.cents) {
      if (cent.waterBalloonHit(this.waterBalloon)) {
        return true;
      }
    }
    return false;
  }

  // EFFECT: modifies the garden and the dart fields of this CGameState
  // alters the state of the game after possible collisions with a dandelion and a
  // dart
  void collidesDandelion() {
    IsDandelion isDandelion = new IsDandelion();
    for (int index = 0; index < this.garden.size(); index += 1) {
      ITile tile = this.garden.get(index);
      if (isDandelion.apply(tile) && this.dart.hitTile(tile)) {
        this.dart = new NoDart();
        this.streak = this.streak + 1;
        tile.lowerHP();
        if (tile.noHP()) {
          this.garden.set(index, new DanToPeb().apply(tile));
        }
      }
    }
  }

  // EFFECT: modifies the centipede, the dart, and the score fields of this
  // CGameState
  // alters the state of the game after possible collisions with a centipede and a
  // dart
  void collidesCentipede() {
    ArrayList<Centipede> cpCent = new ArrayList<>();
    for (Centipede cent : this.cents) {
      if (cent.dartHit(this.dart)) {
        this.score += 10;
        this.streak += 1;
        new Util().append(cpCent, cent.splitDart(this.dart));
        new Util().sproutDandelion(cent.positionHit(this.dart), this.garden);
        this.dart = new NoDart();
      }
      else {
        cpCent.add(cent);
      }
    }
    this.cents.clear();
    for (Centipede cent : cpCent) {
      this.cents.add(cent);
    }
  }

  @Override
  // draws all the elements in the game
  public WorldScene makeScene() {
    WorldScene s = new WorldScene(this.width, this.height);
    for (ITile tile : this.garden) {
      tile.draw(s);
    }
    for (Centipede c : this.cents) {
      c.draw(s);
    }
    this.gnome.draw(s, this.streak);

    this.dart.draw(s);

    this.waterBalloon.draw(s);

    WorldImage streaktext = new TextImage("Water Balloon is Ready", Color.BLUE);
    if (this.streak < 3) {
      streaktext = new TextImage("Streak: " + this.streak, Color.BLUE);
    }
    s.placeImageXY(streaktext, this.width - 8 * ITile.WIDTH / 4, ITile.HEIGHT);

    WorldImage score = new TextImage("Score: " + this.score, Color.BLACK);
    s.placeImageXY(score, this.width - 5 * ITile.WIDTH / 4, ITile.HEIGHT / 4);

    return s;
  }

  @Override
  // EFFECT: modifies the player direction of this CGameState based on the key
  // given by the user
  // moves the player accordingly based on the key input the user gave
  public void onKeyEvent(String s) {
    if (s.equals("left")) {
      this.playerDirection = new Posn(-1, this.playerDirection.y);
    }
    if (s.equals("right")) {
      this.playerDirection = new Posn(1, this.playerDirection.y);
    }

    if (s.equals("up")) {
      this.playerDirection = new Posn(this.playerDirection.x, 1);
    }
    if (s.equals("down")) {
      this.playerDirection = new Posn(this.playerDirection.x, -1);
    }

    if (s.equals(" ")) {
      if (this.dart.offScreen()) {
        this.dart = this.gnome.generateDart();
      }
    }

    if (s.equals("b")) {
      if (this.waterBalloon.offScreen() && streak >= 3) {
        this.score -= 5;
        this.streak = 0;
        this.waterBalloon = this.gnome.generateWaterBallon();
      }
    }
  }

  @Override
  // resets the player's direction to 0 (means not moving) for both components
  public void onKeyReleased(String s) {
    if (s.equals("left") || s.equals("right")) {
      this.playerDirection = new Posn(0, this.playerDirection.y);
    }

    if (s.equals("up") || s.equals("down")) {
      this.playerDirection = new Posn(this.playerDirection.x, 0);
    }
  }

  @Override
  // continues this CGameState to be used in GameMaster
  public CGameState toCGameState() {
    return this;
  }

  // returns the score of this game
  public int score() {
    return this.score;
  }
}