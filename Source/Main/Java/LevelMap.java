import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

final class LevelMap {
    final List<Rectangle> ground = new ArrayList<>();
    final List<Rectangle> solids = new ArrayList<>();
    final List<Rectangle> oneWayPlatforms = new ArrayList<>();
    final List<PlatformObject> objectPlatforms = new ArrayList<>();
    final List<Candy> candies = new ArrayList<>();
    final List<Dog> dogs = new ArrayList<>();
    final Rectangle goalPole = new Rectangle(6880, 270, 10, 160);
    final Rectangle goalArea = new Rectangle(6890, 270, 80, 160);
    private final GameAssets assets;

    LevelMap(GameAssets assets) {
        this.assets = assets;
    }

    void reset() {
        ground.clear();
        solids.clear();
        oneWayPlatforms.clear();
        objectPlatforms.clear();
        candies.clear();
        dogs.clear();

        addGround(0, GameConfig.GROUND_Y, 3150, 110);
        addGround(3210, GameConfig.GROUND_Y, 3890, 110);

        addObject(new RoofPlatform(260, 280, 200, assets.roofImage));
        addObject(new RoofPlatform(710, 280, 200, assets.roofImage));
        addObject(new KarinderyaPlatform(2305, 430, assets.karinderyaImage));
        addObject(new KarinderyaPlatform(3895, 430, assets.karinderyaImage));
        addObject(new KarinderyaPlatform(5800, 430, assets.karinderyaImage));

        addObject(new TrashPlatform(585, 430, assets.trashImage));
        addObject(new TrashPlatform(1595, 430, assets.trashImage));
        addObject(new TrashPlatform(2100, 430, assets.trashImage));
        addObject(new TrashPlatform(2975, 430, assets.trashImage));
        addObject(new TrashPlatform(4335, 430, assets.trashImage));
        addObject(new TrashPlatform(4825, 430, assets.trashImage));

        addObject(new VanPlatform(1070, 430, assets.vanImage));
        addObject(new VanPlatform(3460, 430, assets.vanImage));
        addObject(new VanPlatform(5060, 430, assets.vanImage));
        addObject(new VanPlatform(6540, 430, assets.vanImage));

        addObject(new GatePlatform(1280, 430, assets.gateImage));
        addObject(new GatePlatform(6815, 430, assets.gateImage));

        addObject(new TablePlatform(1740, 430, assets.tableImage));
        addObject(new TablePlatform(1950, 430, assets.tableImage));
        addObject(new TablePlatform(4470, 430, assets.tableImage));
        addObject(new TablePlatform(4680, 430, assets.tableImage));

        addObject(new WarningBlockPlatform(3290, 430, assets.warningBlockImage));

        // Tricycle
        addObject(new TricyclePlatform(2515, 430, assets.tricycleImage));
        addObject(new TricyclePlatform(2675, 430, assets.tricycleImage));
        addObject(new TricyclePlatform(3670, 430, assets.tricycleImage));
        addObject(new TricyclePlatform(4120, 430, assets.tricycleImage));
        addObject(new TricyclePlatform(6030, 430, assets.tricycleImage));

        // It was supposed to be seven eleven but yeah, six seven
        addObject(new SevenEleven(1690, 430, assets.sevenElevenImage));
        addObject(new SevenEleven(4420, 430, assets.sevenElevenImage));
        
        // Building and School
        addObject(new Building(5160, 215, 160, false, assets.buildingImage));
        addObject(new Building(5340, 235, 160, false, assets.buildingImage));
        addObject(new Building(5520, 165, 160, false, assets.buildingImage));
        addObject(new Building(6970, 280, 256, true, assets.schoolImage)); // school building ( its flaotin )

        // Optional candies along the ground and raised routes.
        
        //RoofPlatform One
        addCandy(270, 245); 
        addCandy(350, 245);
        addCandy(430, 245);
        
        //RoofPlatform Two
        addCandy(797, 245);
        
        //VanPlatform One
        addCandy(1000, 285);
        addCandy(1130, 285);
        
        //Near the dog One
        addCandy(780, GameConfig.GROUND_Y - 34);
        addCandy(820, GameConfig.GROUND_Y - 34);
        addCandy(860, GameConfig.GROUND_Y - 34);
       
        // 6-SevenPlatform Store One
        addCandy(1830, GameConfig.GROUND_Y - 34);
        addCandy(1740, 244);
        addCandy(1830, 244);
        
        //TusokTusok One
        addCandy(2280, 255);
        
        //Tricycle One
        addCandy(2450, 285);
        
        //Tricycle Two
        addCandy(2610, 285);
        
        //Near the dog two
        addCandy(2800, GameConfig.GROUND_Y - 34);
        addCandy(2840, GameConfig.GROUND_Y - 34);
        addCandy(2880, GameConfig.GROUND_Y - 34);
        
        //Top of the hole
        addCandy(3160, 360);
        addCandy(3160, 320);
        addCandy(3160, 280);
        
        //VanPlatform Two
        addCandy(3360, 285);
        addCandy(3420, 285);
        addCandy(3480, 285);
        
        //TusokTusok Two
        addCandy(3880, 255);
        
        //Tricycle Three
        addCandy(4115, 285);
        
        //6-SevenPlatform Store Two
        addCandy(4560, GameConfig.GROUND_Y - 34);
        addCandy(4500, 244);
        addCandy(4720, 244);
        
        //VanPlatform Three
        addCandy(4980, 285);
        addCandy(5080, 285);
        
        //BuildingPlatform One
        addCandy(5180, 175);
        addCandy(5220, 175);
        addCandy(5260, 175);
        
        // Aso Three
        addCandy(5300, GameConfig.GROUND_Y - 34);
        addCandy(5380, GameConfig.GROUND_Y - 34);
        addCandy(5460, GameConfig.GROUND_Y - 34);
        addCandy(5540, GameConfig.GROUND_Y - 34);
        addCandy(5620, GameConfig.GROUND_Y - 34);
      
        //BuildingPlatform Two
        addCandy(5360, 195);
        addCandy(5400, 195);
        addCandy(5440, 195);
        
        //BuildingPlatform Three
        addCandy(5540, 125);
        addCandy(5580, 125);
        addCandy(5620, 125);
            
        //TusokTusok Three?
        addCandy(5780, 255);
        
        //Tricycle Three
        addCandy(5967, 285);
        
        //goud
         addCandy(6160, GameConfig.GROUND_Y - 34);
        addCandy(6240, GameConfig.GROUND_Y - 34);
        addCandy(6320, GameConfig.GROUND_Y - 34);
        
        //VanPlatform Four
        addCandy(6440, 285);
        addCandy(6480, 285);
        addCandy(6520, 285);
        addCandy(6560, 285);
        addCandy(6600, 285);

        
        //Aso DOG ASo
        dogs.add(new Dog(790, GameConfig.GROUND_Y - Dog.HEIGHT, 680, 930));
        dogs.add(new Dog(1830, GameConfig.GROUND_Y - Dog.HEIGHT, 1780, 2040));
        dogs.add(new Dog(1830, GameConfig.GROUND_Y - Dog.HEIGHT, 2780, 2860));

        dogs.add(new Dog(2820, GameConfig.GROUND_Y - Dog.HEIGHT, 5200, 5400 ));
        dogs.add(new Dog(2820, GameConfig.GROUND_Y - Dog.HEIGHT, 5450, 5650));
        dogs.add(new Dog(4590, GameConfig.GROUND_Y - Dog.HEIGHT, 4350, 4730));
        dogs.add(new Dog(6340, GameConfig.GROUND_Y - Dog.HEIGHT, 6120, 6400));
    }

    private void addGround(int x, int y, int width, int height) {
        Rectangle section = new Rectangle(x, y, width, height);
        ground.add(section);
        solids.add(section);
    }

    private void addObject(PlatformObject object) {
        objectPlatforms.add(object);
        solids.addAll(object.getCollisionBounds());
        if (object.isOneWay()) {
            oneWayPlatforms.addAll(object.getCollisionBounds());
        }
    }

    private void addCandy(int x, int y) {
        candies.add(new Candy(x, y));
    }
}
