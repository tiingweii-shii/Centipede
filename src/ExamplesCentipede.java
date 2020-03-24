import javalib.worldimages.Posn;
import tester.*;                // The tester library

import java.util.ArrayList;
import java.util.Random;

  /* DOCUMENTATION for known/potential bugs:
     The move() for BodySeg has a slight bug where it will try to move towards a Dandelion
     when moving down sometimes depending on speed. It will re-orient itself to the correct position
     but this leads to janky movement to the eyes. If there was more time, the move() would have
     been fixed by checking if there was a dandelion and adding the excess speed to the y
     position, while mutating all its related fields.

     Speeds may also affect how aheadDandelion() works in the BodySeg class. It currently checks
     if there is a dandelion ahead of this tile, but with higher speeds, it may lead to bugs
     where it may overshoot the current tile, making it not able to detect dandelions correctly.

     If this is the case, then the centipede would break, and all of its segments would not
     follow the head correctly. In certain cases where the body segment somehow reaches
     the top or bottom row before the head, it will also crash. This is because the program
     expects the head to reach those rows first to generate a new ObstacleList to be
     used by all other body segments in the same iteration as that ObstacleList. If the head
     does not reach it first, the ObstacleList with the same iteration would never exist
     and the program would crash.
 */

// tests and examples for the centipede game and all of its related classes and fields
class ExamplesCentipede {
  // examples for everything related to the Centipede game
  IsGrass isGrass = new IsGrass();
  IsDandelion isDandelion = new IsDandelion();
  IsPebble isPebble = new IsPebble();
  DanToPeb danToPeb = new DanToPeb();
  GrassToDan grassToDan = new GrassToDan();
  ArrayList<Integer> onetwothree;
  ArrayList<Integer> none;
  ArrayList<Integer> one;
  ArrayList<Integer> twothree;
  Util util = new Util();
  ITile tile_0;
  ITile tile_1;
  ITile tile_2;
  ITile tile_3;
  ITile tile_4;
  ITile tile_5;
  ITile tile_6;
  ITile tile_7;
  ITile tile_8;
  ITile tile_9;
  ITile tile_10;
  ITile tile_11;
  ITile tile_12;
  ITile tile_13;
  ITile tile_14;
  ArrayList<ITile> garden_0;
  ArrayList<ITile> garden_1;
  ArrayList<ITile> garden_2;
  ArrayList<ITile> garden_3;
  ArrayList<ITile> garden_4;
  IWaterBalloon waterBalloon_0;
  IWaterBalloon waterBalloon_1;
  IWaterBalloon waterBalloon_2;
  IWaterBalloon waterBalloon_3;
  IDart dart_0;
  IDart dart_1;
  IDart dart_2;
  IDart dart_3;
  ArrayList<BodySeg> bseg_0;
  ArrayList<BodySeg> bseg_1;
  BodySeg bodySeg_0;
  BodySeg bodySeg_1;
  BodySeg bodySeg_2;
  BodySeg bodySeg_3;
  Centipede cent_0;
  Centipede cent_1;
  Centipede cent_2;
  Centipede cent_3;
  ObstacleList obl_0;
  ObstacleList obl_1;
  ObstacleList obl_2;
  Gnome player;
  CGameState cgame_0;
  CGameState cgame_1;
  BGameState bgame_0;
  BGameState bgame_1;
  GameState gamestate_0;
  GameState gamestate_1;
  GameMaster gm_0;
  GameMaster gm_1;

  // initializes test conditions
  void initTestConditions() {
    onetwothree = new ArrayList<>();
    onetwothree.add(1);
    onetwothree.add(2);
    onetwothree.add(3);
    none = new ArrayList<>();
    one = new ArrayList<>();
    one.add(1);
    twothree = new ArrayList<>();
    twothree.add(2);
    twothree.add(3);
    tile_0 = new GrassTile(20, 60, 400);
    tile_1 = new PebbleTile(20, 20, 400);
    tile_2 = new DandelionTile(60, 60, 3, 400);
    tile_3 = new GrassTile(100, 100, 400);
    tile_4 = new PebbleTile(140, 180, 400);
    tile_5 = new GrassTile(20, 20, 40);
    tile_6 = new GrassTile(20, 20, 80);
    tile_7 = new GrassTile(20, 60, 80);
    tile_8 = new GrassTile(60, 20, 80);
    tile_9 = new GrassTile(60, 60, 80);
    tile_10 = new DandelionTile(20, 60, 2, 80);
    tile_11 = new DandelionTile(60, 60, 1, 80);
    tile_12 = new DandelionTile(60, 20, 3, 80);
    tile_13 = new PebbleTile(60, 20, 400);
    tile_14 = new PebbleTile(380, 20, 400);
    garden_0 = new ArrayList<>();
    garden_0.add(tile_0);
    garden_0.add(tile_1);
    garden_0.add(tile_2);
    garden_0.add(tile_3);
    garden_1 = new ArrayList<>();
    garden_1.add(tile_5);
    garden_2 = new ArrayList<>();
    garden_2.add(tile_6);
    garden_2.add(tile_7);
    garden_2.add(tile_8);
    garden_2.add(tile_9);
    garden_3 = util.generateGrassBoard(3, 3);
    garden_4 = util.generateGrassBoard(5, 5);
    waterBalloon_0 = new WaterBalloon(0, 0, 10);
    waterBalloon_1 = new WaterBalloon(2, 1, 3);
    waterBalloon_2 = new WaterBalloon(2, 3, 4);
    waterBalloon_3 = new NoWaterBalloon();
    dart_0 = new Dart(0, 0, 10);
    dart_1 = new Dart(2, 1, 11);
    dart_2 = new Dart(5, 5, 10);
    dart_3 = new NoDart();

    bseg_0 = new ArrayList<>();
    bodySeg_0 = new BodySeg(new Posn(380, 60), new Posn(0, 6),
        false, true, false, 60, 0);
    bodySeg_1 = new BodySeg(new Posn(340, 60), new Posn(0, 2),
        false, true, false, 60, 0);
    bseg_0.add(bodySeg_0);
    bseg_0.add(bodySeg_1);

    bseg_1 = new ArrayList<>();
    bodySeg_2 = new BodySeg(new Posn(380, 20), new Posn(0, 2),
        false, true, false, 60, 0);
    bodySeg_3 = new BodySeg(new Posn(376, 20), new Posn(0, 2),
        false, true, false, 60, 0);
    bseg_1.add(bodySeg_2);
    bseg_1.add(bodySeg_3);

    cent_0 = new Centipede(10, 4);
    cent_1 = new Centipede(bseg_0, 8,
        10, new ArrayList<>(), new ArrayList<>());
    cent_2 = new Centipede(bseg_0, 8,
        10, new ArrayList<>(), new ArrayList<>());
    cent_3 = new Centipede(1, 4);

    ArrayList<Posn> posns_0 = new ArrayList<>();
    posns_0.add(new Posn(280, 60));
    obl_0 = new ObstacleList(0, posns_0);

    ArrayList<Posn> posns_1 = new ArrayList<>();
    posns_1.add(new Posn(20, 20));
    obl_1 = new ObstacleList(1, posns_1);
    obl_2 = new ObstacleList(2, new ArrayList<>());

    player = new Gnome(20, 15 * ITile.HEIGHT - ITile.HEIGHT / 2, ITile.WIDTH / 7);

    cgame_0 = new CGameState(3, 3, garden_1, player);
    cgame_1 = new CGameState(5, 5, garden_2, player);

    bgame_0 = new BGameState(3, 3, 20);
    bgame_1 = new BGameState(3, 3, garden_1, player, new Random(20));

    gamestate_0 = cgame_0;
    gamestate_1 = bgame_0;

    gm_0 = new GameMaster(gamestate_0);
    gm_1 = new GameMaster(gamestate_1);
  }

  // function object tests

  // tests IsGrass, IsPebble, IsDandelion apply(ITile)
  // NOTE: all the other methods in these ITileVisitor's are tested implicitly
  boolean testIsXApply(Tester t) {
    this.initTestConditions();
    IsGrass isGrass = new IsGrass();
    IsPebble isPebble = new IsPebble();
    IsDandelion isDandelion = new IsDandelion();

    return t.checkExpect(isGrass.apply(tile_0), true)
        && t.checkExpect(isPebble.apply(tile_0), false)
        && t.checkExpect(isDandelion.apply(tile_0), false)
        && t.checkExpect(isGrass.apply(tile_1), false)
        && t.checkExpect(isPebble.apply(tile_1), true)
        && t.checkExpect(isDandelion.apply(tile_1), false)
        && t.checkExpect(isGrass.apply(tile_2), false)
        && t.checkExpect(isPebble.apply(tile_2), false)
        && t.checkExpect(isDandelion.apply(tile_2), true);

  }

  // tests GrassToDan apply(ITile)
  // NOTE: all the other methods in this ITileVisitor's are tested implicitly
  boolean testGrassToDanApply(Tester t) {
    GrassToDan grassToDan = new GrassToDan();
    return t.checkExpect(grassToDan.apply(tile_0), new DandelionTile(20, 60, 3, 400))
        && t.checkExpect(grassToDan.apply(tile_1), tile_1)
        && t.checkExpect(grassToDan.apply(tile_2), tile_2);
  }

  // tests DanToPeb apply(ITile)
  // NOTE: all the other methods in these ITileVisitor's are tested implicitly
  boolean testDanToPebApply(Tester t) {
    DanToPeb danToPeb = new DanToPeb();
    return t.checkExpect(danToPeb.apply(tile_0), tile_0)
        && t.checkExpect(danToPeb.apply(tile_1), tile_1)
        && t.checkExpect(danToPeb.apply(tile_2), new PebbleTile(60, 60, 400));
  }

  // tests for the util class

  // tests util generateCentBody(int, int)
  void testUtilGenerateCentBody(Tester t) {
    ArrayList<BodySeg> body = new ArrayList<>();
    body.add(new BodySeg(new Posn(-60, 20),
        new Posn(4, 0), false, true, true, 60, 0));
    body.add(new BodySeg(new Posn(-20, 20),
        new Posn(4, 0), false, true, true, 60, 0));
    BodySeg head = new BodySeg(new Posn(20, 20),
        new Posn(4, 0), true, true, true, 60, 0);
    body.add(head);
    t.checkExpect(util.generateCentBody(3, 4), body);
    body.remove(0);
    t.checkExpect(util.generateCentBody(2, 4), body);
    body.remove(0);
    t.checkExpect(util.generateCentBody(1, 4), body);
  }

  // tests util getElementsBetween(ArrayList<T>, int, int)
  void testUtilGetElementsBetween(Tester t) {
    this.initTestConditions();
    t.checkExpect(util.getElementsBetween(onetwothree, 0, 1), one);
    t.checkExpect(util.getElementsBetween(onetwothree, 0, 0), none);
    t.checkExpect(util.getElementsBetween(onetwothree, 1, 3), twothree);
    t.checkExpect(util.getElementsBetween(onetwothree, 3, 2), none);
  }

  // tests util singletonList(T)
  boolean testUtilSingletonList(Tester t) {
    this.initTestConditions();
    return t.checkExpect(util.singletonList(1), one);
  }

  // tests util generateGrassBoard(int, int)
  // NOTE: this checks generateGrassRow(ArrayList<ITile>, int, int, int) implicitly
  boolean testUtilGenerateGrassBoard(Tester t) {
    this.initTestConditions();
    return t.checkExpect(util.generateGrassBoard(1, 1), garden_1)
        && t.checkExpect(util.generateGrassBoard(2, 2), garden_2)
        && t.checkExpect(util.generateGrassBoard(0, 0), new ArrayList<>());
  }

  // tests util copy(ArrayList<T>)
  void testUtilCopy(Tester t) {
    this.initTestConditions();
    ArrayList<Integer> one = new ArrayList<>();
    one.add(1);
    t.checkExpect(util.copy(this.one), one);
    t.checkExpect(one == this.one, false);
  }

  // tests util append(ArrayList<T>, ArrayList<T>)
  void testUtilAppend(Tester t) {
    this.initTestConditions();
    util.append(one, twothree);
    t.checkExpect(one, onetwothree);
    util.append(none, one);
    t.checkExpect(none, onetwothree);

    this.initTestConditions();
    ArrayList<Integer> one = new ArrayList<>();
    one.add(1);
    util.append(this.one, none);
    t.checkExpect(this.one, one);
  }

  // tests util inRange(Posn, Posn)
  boolean utilInRange(Tester t) {
    return t.checkExpect(util.inRange(new Posn(0, 0), new Posn(20, 20)), true)
        && t.checkExpect(util.inRange(new Posn(100, 100), new Posn(20, 20)), false)
        && t.checkExpect(util.inRange(new Posn(60, 60), new Posn(60, 60)), true)
        && t.checkExpect(util.inRange(new Posn(101, 100), new Posn(60, 60)), false);
  }

  // tests util sproutDandelions(Posn, ArrayList<ITile>)
  void testUtilSproutDandelions(Tester t) {
    this.initTestConditions();
    ArrayList<ITile> changedGarden = new ArrayList<>();
    changedGarden.add(new DandelionTile(20, 20, 3, 40));
    util.sproutDandelion(new Posn(20, 20), garden_1);
    t.checkExpect(garden_1, changedGarden);

    changedGarden = new ArrayList<>();
    changedGarden.add(tile_6);
    changedGarden.add(new DandelionTile(20, 60, 3, 80));
    changedGarden.add(tile_8);
    changedGarden.add(tile_9);

    util.sproutDandelion(new Posn(20, 60), garden_2);
    t.checkExpect(garden_2, changedGarden);
  }

  // tests util sproutDanInPosns(ArrayList<Posn>, ArrayList<ITile>)
  void testUtilSproutDanInPosns(Tester t) {
    this.initTestConditions();
    ArrayList<ITile> changedGarden = new ArrayList<>();
    changedGarden.add(new DandelionTile(20, 20, 3, 40));
    ArrayList<Posn> hitbox = new ArrayList<>();
    hitbox.add(new Posn(20, 20));
    util.sproutDanInPosns(hitbox, garden_1);
    t.checkExpect(garden_1, changedGarden);

    changedGarden = new ArrayList<>();
    changedGarden.add(tile_6);
    changedGarden.add(new DandelionTile(20, 60, 3, 80));
    changedGarden.add(new DandelionTile(60, 20, 3, 80));
    changedGarden.add(tile_9);

    hitbox = new ArrayList<>();
    hitbox.add(new Posn(20, 60));
    hitbox.add(new Posn(60, 20));

    util.sproutDanInPosns(hitbox, garden_2);
    t.checkExpect(garden_2, changedGarden);
  }

  // tests for ITile interface

  // draw(WorldScene) can be seen in the big bang world

  // tests ITile samePos(Posn)
  boolean testITileSamePos(Tester t) {
    this.initTestConditions();
    return t.checkExpect(tile_1.samePos(new Posn(20, 20)), true)
        && t.checkExpect(tile_1.samePos(new Posn(20, 21)), false);
  }

  // tests ITile replaceTile(String, int)
  boolean testITileReplaceTile(Tester t) {
    this.initTestConditions();
    return t.checkExpect(tile_0.replaceTile("LeftButton", 380),
        new DandelionTile(20, 60, 3, 400))
        && t.checkExpect(tile_0.replaceTile("RightButton", 380),
        new PebbleTile(20, 60, 400))
        && t.checkExpect(tile_1.replaceTile("LeftButton", 380),
        new GrassTile(20, 20, 400))
        && t.checkExpect(tile_2.replaceTile("LeftButton", 380),
        new GrassTile(60, 60, 400))
        && t.checkExpect(tile_2.replaceTile("RightButton", 380), tile_2)
        && t.checkExpect(tile_1.replaceTile("LeftButton", 20), tile_1);
  }

  // accept(ITileVisitor) is tested implicitly by other ITileVistor

  // tests ITile lowerHP()
  void testITileLowerHP(Tester t) {
    this.initTestConditions();
    ITile pebbleTile = new PebbleTile(20, 20, 400);
    tile_1.lowerHP();
    t.checkExpect(tile_1, pebbleTile);

    ITile dandelionTile = new DandelionTile(60, 60, 2, 400);
    tile_2.lowerHP();
    t.checkExpect(tile_2, dandelionTile);

    ITile grassTile = new GrassTile(100, 100, 400);
    tile_3.lowerHP();
    t.checkExpect(tile_3, grassTile);
  }

  // tests ITile fullHP()
  void testITileFullHP(Tester t) {
    this.initTestConditions();
    ITile pebbleTile = new PebbleTile(20, 20, 400);
    tile_1.fullHP();
    t.checkExpect(tile_1, pebbleTile);

    ITile dan_0 = new DandelionTile(20, 60, 3, 80);
    ITile dan_1 = new DandelionTile(60, 60, 3, 80);
    ITile dan_2 = new DandelionTile(60, 20, 3, 80);
    tile_10.fullHP();
    tile_11.fullHP();
    tile_12.fullHP();
    t.checkExpect(tile_10, dan_0);
    t.checkExpect(tile_11, dan_1);
    t.checkExpect(tile_12, dan_2);

    ITile grassTile = new GrassTile(100, 100, 400);
    tile_3.fullHP();
    t.checkExpect(tile_3, grassTile);
  }

  // tests ITile noHP()
  boolean testITileNoHP(Tester t) {
    this.initTestConditions();
    ITile dan_0 = new DandelionTile(20, 20, 0, 3);
    return t.checkExpect(tile_10.noHP(), false)
        && t.checkExpect(dan_0.noHP(), true)
        && t.checkExpect(tile_1.noHP(), false);
  }

  // tests ITile hitBox()
  boolean testITileHitBox(Tester t) {
    this.initTestConditions();
    ArrayList<Posn> hitBoxOne = new ArrayList<>();
    ArrayList<Posn> hitBoxTwo = new ArrayList<>();
    ArrayList<Posn> hitBoxThree = new ArrayList<>();
    ArrayList<Posn> hitBoxFour = new ArrayList<>();
    hitBoxOne.add(new Posn(20, 20));
    hitBoxOne.add(new Posn(60, 20));
    hitBoxOne.add(new Posn(20, 60));

    hitBoxTwo.add(new Posn(140, 180));
    hitBoxTwo.add(new Posn(100, 180));
    hitBoxTwo.add(new Posn(180, 180));
    hitBoxTwo.add(new Posn(140, 140));
    hitBoxTwo.add(new Posn(140, 220));

    hitBoxThree.add(new Posn(60, 20));
    hitBoxThree.add(new Posn(20, 20));
    hitBoxThree.add(new Posn(100, 20));
    hitBoxThree.add(new Posn(60, 60));

    hitBoxFour.add(new Posn(380, 20));
    hitBoxFour.add(new Posn(340, 20));
    hitBoxFour.add(new Posn(380, 60));

    return t.checkExpect(tile_2.hitBox(), util.singletonList(new Posn(60, 60)))
        && t.checkExpect(tile_1.hitBox(), hitBoxOne)
        && t.checkExpect(tile_4.hitBox(), hitBoxTwo)
        && t.checkExpect(tile_13.hitBox(), hitBoxThree)
        && t.checkExpect(tile_14.hitBox(), hitBoxFour);
  }

  // tests ITile inRange()
  void testITileInRange(Tester t) {
    this.initTestConditions();
    Posn first = new Posn(21, 21);
    Posn second = new Posn(380, 225);
    Posn third = new Posn(62, 60);
    Posn fourth = new Posn(342, 20);
    Posn fifth = new Posn(20, 20);

    t.checkExpect(tile_1.inRange(first), true);
    t.checkExpect(tile_4.inRange(second), false);
    t.checkExpect(tile_13.inRange(third), true);
    t.checkExpect(tile_14.inRange(fourth), true);
    t.checkExpect(tile_1.inRange(fifth), true);
  }

  // tests for IProjectile

  // draw(WorldScene) can be seen in the big bang world

  // runs the game - the setup first, then the game by pressing "s"
  void testBigBang(Tester t) {
    int x = 10;
    int y = 15;
    GameMaster w = new GameMaster(x, y, 20);
    w.bigBang(x * ITile.WIDTH, y * ITile.HEIGHT, 1.0 / 28.0);
  }
}
