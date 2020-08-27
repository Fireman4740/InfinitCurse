import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class main extends PApplet {

//float angle, tangente;
public void setup() {
  background(0);
  
  frameRate(60);
  
  
  //setup tir
  speedBallEnnemie = 10;
  speedBallPlayer = 10;
  sizeBallPlayer = 10;
  speedPlayer = 5;
  sizePlayer = 20;
  sizeSpitter = 30;
  speedSpitter = 2;
  playerBallDamageMax = 1;
  
  //vie ennemie
  baseSpitterHP = 1;
  
  //prix et gold joueur
  gold = 150;
  priceBulletSize = 25;
  priceBulletBounce = 500;
  priceIncreaseHP = 500;
  priceBulletDamage = 100;
  
  //images
  heart_full_for_pickup =loadImage("8Bit_Heart_Full.png");
  heart_full= loadImage("8Bit_Heart_Full.png");
  heart_half = loadImage("8Bit_Heart_Half.png");
  heart_empty = loadImage("8Bit_Heart_Empty.png");
  heart_full.resize(0,50);
  heart_half.resize(0,50);
  heart_empty.resize(0,50);
  
  BallPlayerType1 = new ArrayList<BallPlayer>();
  BallEnnemieType1 = new ArrayList<BallEnnemie>();
  Spitter = new ArrayList<Ennemies>();
  HeartsPickup = new ArrayList<HealthPack>();
  
  //setup coeur de vie
  hearts = new IntList();
  for(int i=0; i<4; i++) {
  hearts.append(0); }
  sizeHeartsPickup = 30;
  
}

public void draw()
{
  game();
}
ArrayList<Ennemies> Spitter;
//Ennemies variables
float angleEnnemies;  //angle ennemies
int speedSpitter, sizeSpitter;//vitesse et taille general des Spitter
int baseSpitterHP;
boolean ennemieKilled;

class Ennemies
{
  int posEnnemiesX, posEnnemiesY, speedSpitterX, speedSpitterY, delay_shot_ennemie, spitterHP;
  public Ennemies(int startX, int startY, int startSpeedSpitterX, int startSpeedSpitterY, int startSpitterHP)
  {
    posEnnemiesX = startX;
    posEnnemiesY = startY;
    speedSpitterX = startSpeedSpitterX;
    speedSpitterY = startSpeedSpitterY;
    spitterHP = startSpitterHP;
  }

  public void show()
  {
    angleEnnemies = atan2(player1.posPlayerY-posEnnemiesY, player1.posPlayerX-posEnnemiesX);
    pushMatrix();
    translate(posEnnemiesX, posEnnemiesY);
    rotate(angleEnnemies);
    noStroke();
    fill(255, 0, 0);
    ellipse(0, 0, sizeSpitter, sizeSpitter);
    rectMode(CENTER);
    rect(sizeSpitter*0.8f, 0, sizeSpitter*0.8f, sizeSpitter*0.6f);
    popMatrix();
  }
  
  public void move()
  {
    posEnnemiesX += speedSpitterX;
    posEnnemiesY += speedSpitterY;
    
    if (whatsUnderneath(posEnnemiesX+(sizeSpitter/2), posEnnemiesY) != 0)
    {
      speedSpitterY = PApplet.parseInt(random(-speedSpitter,speedSpitter));
      speedSpitterX = PApplet.parseInt(random(-speedSpitter,0));
    }
    if (whatsUnderneath(posEnnemiesX, posEnnemiesY+(sizeSpitter/2)) != 0)
    {
      speedSpitterY = PApplet.parseInt(random(-speedSpitter,0));
      speedSpitterX = PApplet.parseInt(random(-speedSpitter,speedSpitter));
    }
    if (whatsUnderneath(posEnnemiesX-(sizeSpitter/2), posEnnemiesY) != 0) 
    {
      speedSpitterY = PApplet.parseInt(random(-speedSpitter,speedSpitter));
      speedSpitterX = PApplet.parseInt(random(0,speedSpitter));
    }
    if (whatsUnderneath(posEnnemiesX, posEnnemiesY-(sizeSpitter/2)) != 0) 
    {
      speedSpitterY = PApplet.parseInt(random(0,speedSpitter));
      speedSpitterX = PApplet.parseInt(random(-speedSpitter,speedSpitter));
    }
  }
  
  public void shoot()
  {
    delay_shot_ennemie += 1;
    if (delay_shot_ennemie == 1)
    {
      BallEnnemieType1.add(new BallEnnemie(posEnnemiesX, posEnnemiesY));
    }
    if (delay_shot_ennemie == 30)
    {
      delay_shot_ennemie = 0;
    }
  }
  
  public void destruction()
  {
    for (int i=BallPlayerType1.size()-1; i>=0; i--)
    {
      BallPlayer current_BallPlayer=BallPlayerType1.get(i);
      if ((current_BallPlayer.posBallPlayerX-(sizeBallPlayer/2) <= posEnnemiesX+(sizeSpitter/2) && current_BallPlayer.posBallPlayerX+(sizeBallPlayer/2) >= posEnnemiesX-(sizeSpitter/2)) 
      && (current_BallPlayer.posBallPlayerY-(sizeBallPlayer/2) <= posEnnemiesY+(sizeSpitter/2) && current_BallPlayer.posBallPlayerY+(sizeBallPlayer/2) >= posEnnemiesY-(sizeSpitter/2)))
      {
        int diff = current_BallPlayer.playerBallDamage - spitterHP;
        spitterHP -= current_BallPlayer.playerBallDamage;
        current_BallPlayer.playerBallDamage = diff;
        if (current_BallPlayer.playerBallDamage <= 0) BallPlayerType1.remove(i);
        if (spitterHP <= 0) ennemieKilled = true;
        else ennemieKilled = false;
      }
    }
  }
}
ArrayList<HealthPack> HeartsPickup;
int sizeHeartsPickup;
boolean healthPickedUp;
PImage heart_full_for_pickup;

class HealthPack
{
  int posHealthPackX, posHealthPackY;
  public HealthPack (int startX, int startY)
  {
    posHealthPackX = startX;
    posHealthPackY = startY;
  }
  
  public void show()
  {
    heart_full_for_pickup.resize(0,sizeHeartsPickup);
    image(heart_full_for_pickup, posHealthPackX-(sizeHeartsPickup/2), posHealthPackY-(sizeHeartsPickup/2));
  }
  
  public void destruction()
  {
    if (player1.posPlayerX-(sizePlayer/2) <= posHealthPackX+(sizeHeartsPickup/2) && player1.posPlayerX+(sizePlayer/2) >= posHealthPackX-(sizeHeartsPickup/2)
    && player1.posPlayerY-(sizePlayer/2) <= posHealthPackY+(sizeHeartsPickup/2) && player1.posPlayerY+(sizePlayer/2) >= posHealthPackY-(sizeHeartsPickup/2))
    {
      healthPickedUp = true;
      if (player1.player_HP <= player1.player_MaxHP)
      {
        player1.player_HP += 1;
      }
    }
    else healthPickedUp = false;
  }
}

PImage heart_full;
PImage heart_half;
PImage heart_empty;

int max_HP = 10;
int nb_of_hearts;
IntList hearts;

public void hearts()
{
  nb_of_hearts = ((max_HP - (max_HP % 2)) /2)+1;
  
  for(int i=0; i<nb_of_hearts; i++)
  {    
    hearts.set(i, player1.player_HP-2*i);
  }
}

public void images()
{  
  int ecart = (tile_size-55)/2;
  for(int i=0; i<hearts.size()-1; i++)
  {
    if(hearts.get(i)>=2)
    {
      image(heart_full, i*tile_size + ecart, height-60);
    }
    
    if(hearts.get(i)==1)
    {
      image(heart_half, i*tile_size + ecart, height-60);
    }
    
    if(hearts.get(i)<=0)
    {
      image(heart_empty, i*tile_size + ecart, height-60);
    }
  }
}
boolean holdZ=false, holdQ=false, holdS=false, holdD=false;  //touches clavier
boolean HoldM1=false; //touche souris

public void keyPressed()
{
  //player movement
  if (key=='z')
  {
    holdZ=true;
  }

  if (key=='q')
  {
    holdQ=true;
  }

  if (key=='s')
  {
    holdS=true;
  }

  if (key=='d')
  {
    holdD=true;
  }

  //spitter spawn
  if (key=='x')
  {
    if (whatsUnderneath(mouseX, mouseY) == 0)
    {
      Spitter.add(new Ennemies(mouseX, mouseY, PApplet.parseInt(random(-speedSpitter,speedSpitter)), PApplet.parseInt(random(-speedSpitter,speedSpitter)), baseSpitterHP));
      ennemieKilled = false;
    }
  }
}

public void keyReleased()
{
  if (key=='z')
  {
    holdZ=false;
  }

  if (key=='q')
  {
    holdQ=false;
  }

  if (key=='s')
  {
    holdS=false;
  }

  if (key=='d')
  {
    holdD=false;
  }
}

public void mousePressed()
{
  //player shooting
  if (mouseButton == LEFT)
  {
    HoldM1 = true;
  }
}

public void mouseReleased()
{
  //player shooting
  if (mouseButton == LEFT)
  {
    HoldM1 = false;
  }
}
public void game()
{
  background(125);
  images();
  hearts();
  collisions_wall();
  exit_room();
  worldGridDraw();
  shop();
  
  anglePlayer = atan2(mouseY-player1.posPlayerY,mouseX-player1.posPlayerX);
  
  player1.show();
  player1.gameOver();
  
  //lecture de chaque Spitter pour les faire apparaitre et déplacer
  for (int i=Spitter.size()-1; i>=0; i--)
  {
    Ennemies current_Ennemies=Spitter.get(i);
    current_Ennemies.show();
    current_Ennemies.move();
    current_Ennemies.destruction();
    current_Ennemies.shoot();
    if ( ennemieKilled == true) 
    {
      if (random(0,100) <= 10)  //10% de chance de faire apparaitre de la vie aus sol
      {
        HeartsPickup.add(new HealthPack(current_Ennemies.posEnnemiesX,current_Ennemies.posEnnemiesY));
      }
      Spitter.remove(i);  //suppression du Spitter
      gold += 25;  //25 gold par ennemi tué
    }
    ennemieKilled = false;
  }
  
  //lecture de chaque balle joueur pour les faire apparaitre et déplacer
  for (int i=BallPlayerType1.size()-1; i>=0; i--)
  {   
    BallPlayer current_BallPlayer=BallPlayerType1.get(i);
    current_BallPlayer.show();
    current_BallPlayer.move();
    current_BallPlayer.destruction();
    if ( ballPlayerWall == true) BallPlayerType1.remove(i);  //suppression de la balle
  }
  
  //lecture de chaque balle ennemie pour les faire apparaitre et déplacer
  for (int i=BallEnnemieType1.size()-1; i>=0; i--)
  {   
    BallEnnemie current_BallEnnemie=BallEnnemieType1.get(i);
    current_BallEnnemie.show();
    current_BallEnnemie.move();  
    current_BallEnnemie.destruction();
    if(abs(current_BallEnnemie.posBallEnnemieX-player1.posPlayerX)<=10 && abs(current_BallEnnemie.posBallEnnemieY-player1.posPlayerY)<=10)
    {
      player1.player_HP -= 1; //suppresion de 1 point de vie si contact avec le joueur
      BallEnnemieType1.remove(i);  //suppression de la balle si contact avec le joueur
    }
    if ( ballEnnemieWall == true) BallEnnemieType1.remove(i);  //suppression de la balle si contact avec le mur
  }
  
  //lecture de chaque coeur pour les faire apparaitre
  for (int i=HeartsPickup.size()-1; i>=0; i--)
  {
    HealthPack current_HealthPack = HeartsPickup.get(i);
    current_HealthPack.show();
    current_HealthPack.destruction();
    if(healthPickedUp == true) HeartsPickup.remove(i);  //suppression du coeur après récupération
  }
  
  inputState();
}
//player variables
player player1= new player(455, 810);  //initialisation du joueur
float anglePlayer;  //angle joueur
int DelayShotPlayer, sizePlayer, speedPlayer, quantityEnnemie, level, every5level, gold;

class player
{
  int posPlayerX;
  int posPlayerY;
  int player_HP = 10;
  int player_MaxHP = 10;
  public player(int startX, int startY)
  {
    posPlayerX = startX;
    posPlayerY = startY;
  }

  public void show()
  {
    pushMatrix();
    translate(player1.posPlayerX, player1.posPlayerY);
    rotate(anglePlayer);
    noStroke();
    fill(34, 214, 44);
    ellipse(0, 0, sizePlayer, sizePlayer);
    rectMode(CENTER);
    rect(sizePlayer*0.8f, 0, sizePlayer*0.8f, sizePlayer*0.6f);
    popMatrix();
    
    //affichage gold
    fill(255);
    textSize(50);
    text("Gold :"+gold,width-300,height-20);
  }

  public void Move_player_x(int speedPlayer)
  {
    posPlayerX += speedPlayer;
  }

  public void Move_player_y(int speedPlayer)
  {
    posPlayerY += speedPlayer;
  }
  
  public void gameOver()
  {    
    if(player1.player_HP <= 0)
    {
      noLoop();
      for (int i=Spitter.size()-1; i>=0; i--)
      {
        Spitter.remove(i);        
      }
      for (int i=BallEnnemieType1.size()-1; i>=0; i--)
      {
        BallEnnemieType1.remove(i);
      }
      for (int i=BallPlayerType1.size()-1; i>=0; i--)
      {
        BallPlayerType1.remove(i);
      }
      background(0);
      fill(255);
      textSize(20);
      text("GAME OVER",width/2-50,height/2-20);
    }
  }
}

//fonction pour détecter les collisions avec les murs
public void collisions_wall()
{  
  if (whatsUnderneath(player1.posPlayerX+(sizePlayer/2), player1.posPlayerY) == 1)
  {
    wallOnTheRight = true;
  } else {
    wallOnTheRight = false;
  }

  if (whatsUnderneath(player1.posPlayerX, player1.posPlayerY+(sizePlayer/2)) == 1)
  {
    wallOnBottom = true;
  } else {
    wallOnBottom = false;
  }

  if (whatsUnderneath(player1.posPlayerX-(sizePlayer/2), player1.posPlayerY) == 1) 
  {
    wallOnTheLeft = true;
  } else {
    wallOnTheLeft = false;
  }

  if (whatsUnderneath(player1.posPlayerX, player1.posPlayerY-(sizePlayer/2)) == 1) 
  {
    wallOnTop = true;
  } else {
    wallOnTop = false;
  }
}

//fonction pour détecter les collisions avec les portes de sorti
public void exit_room()
{
  if (whatsUnderneath(player1.posPlayerX, player1.posPlayerY) == 2)
  {
    onExitDoor = true;
  } else {
    onExitDoor = false;
  }
  
  // niveau accomplis donc passage au niveau suivant quand le joueur passe par la porte.
  if (onExitDoor == true && Spitter.size() == 0)
  {
    for (int i=0; i<=quantityEnnemie; i++)
    {
      Spitter.add(new Ennemies(PApplet.parseInt(random(100,width-100)), PApplet.parseInt(random(100,height/2)),
      PApplet.parseInt(random(-speedSpitter,speedSpitter)), PApplet.parseInt(random(-speedSpitter,speedSpitter)), baseSpitterHP));
      ennemieKilled = false;
    }
    every5level += 1;
    if (PApplet.parseInt(every5level/5) == 1)
    {
      every5level = 0;
      baseSpitterHP += 1;
    }
    level += 1;
    quantityEnnemie += 1;
    println(level);
    player1.posPlayerY=height-170;
    player1.posPlayerX=width/2;
  }
  
  // teleportation au centre si niveux non terminé
  else
  {
    if (onExitDoor == true)
    {
      player1.posPlayerY=height/2;
      player1.posPlayerX=width/2;
    }
  }
}

//confirmation possibilité de mouvement
public void inputState()
{
  if (holdZ==true && wallOnTop == false) player1.Move_player_y(-speedPlayer);

  if (holdQ==true && wallOnTheLeft == false) player1.Move_player_x(-speedPlayer);

  if (holdS==true && wallOnBottom == false) player1.Move_player_y(speedPlayer);

  if (holdD==true && wallOnTheRight == false) player1.Move_player_x(speedPlayer);
  
  //tir joueur
  if (HoldM1 == true && whatsUnderneath(player1.posPlayerX, player1.posPlayerY) != 3)
  {
    //timer
    DelayShotPlayer += 1;
    if (DelayShotPlayer == 1)
    {
      BallPlayerType1.add(new BallPlayer(player1.posPlayerX, player1.posPlayerY, playerBallDamageMax));
    }
    if (DelayShotPlayer == 30)
    {
      DelayShotPlayer = 0;
    }
  }
  else
  {
    DelayShotPlayer = 0;
  }
}
final int tile_size = 70;  //changer valeur pour changer la longueur des côtés des tiles.
final int GridFree = 0;  //case vide où on peut marcher
final int GridWall = 1;  // murs
final int GridExitDoor = 2;  //porte de sorti
final int GridShop = 3;  //porte de sorti

boolean wallOnTheRight, wallOnTheLeft, wallOnBottom, wallOnTop, onExitDoor;  //variable true/false pour les collisions avec la matrice

int [][] worldGrid = 
  { {1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1}, //la carte 
    {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}, 
    {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}, 
    {1, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 1}, 
    {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}, 
    {1, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 1}, 
    {2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2}, 
    {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}, 
    {1, 1, 1, 1, 0, 0, 0, 0, 0, 1, 1, 1, 1}, 
    {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}, 
    {1, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 1}, 
    {1, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 1}, 
    {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}};
    
//fonction qui trouve la case sur laquelle on est
public int whatsUnderneath(int FindCol, int FindRow)
{
  int someTileRow = FindRow / tile_size;  //retourne l'indice de la ligne où on est
  int someTileCol = FindCol / tile_size;  //retourne l'indice de la colonne où on est
  return worldGrid[someTileRow][someTileCol];
}

//fonction pour dessiner la carte
public void worldGridDraw()
{
  for (int row=0; row<13; row++)
  {
    for (int col=0; col<13; col++)
    {
      if (worldGrid[row][col] == 1)
      {
        fill(0);
      } 
      else if (worldGrid[row][col] == 0)
      {
        fill(255);
      } 
      else if (worldGrid[row][col] == 2)
      {
        if (Spitter.size() == 0)
        {
          fill(0, 0, 255);
        }
        else
        {
          fill(255, 0, 0);
        }
      }
      else if (worldGrid[row][col] == 3)
      {
        if (Spitter.size() == 0)
        {
          fill(255, 255, 0);
        }
        else fill(131, 0, 255);
      }
      rectMode(CORNER);
      stroke(50);
      rect(col*tile_size, row*tile_size, tile_size, tile_size);
    }
  }
}
ArrayList<BallPlayer> BallPlayerType1;
ArrayList<BallEnnemie> BallEnnemieType1;
boolean ballPlayerWall, ballEnnemieWall, playerBallBounce;
int speedBallPlayer, speedBallEnnemie, sizeBallPlayer, playerBallDamageMax;

class BallPlayer
{
  float posBallPlayerX, posBallPlayerY;
  PVector VectoredspeedBallPlayer = new PVector(speedBallPlayer*cos(anglePlayer), speedBallPlayer*sin(anglePlayer));  //calcul du vecteur de vitesse de la balle
  float speedBallPlayerX = VectoredspeedBallPlayer.x;
  float speedBallPlayerY = VectoredspeedBallPlayer.y;
  int playerBallBounceCount, playerBallDamage;
  
  public BallPlayer(int startX, int startY, int startPlayerBallDamage)
  {
    posBallPlayerX=startX;
    posBallPlayerY=startY;
    if (playerBallBounce == true)
    {
      playerBallBounceCount = 1;
    }
    playerBallDamage = startPlayerBallDamage;
  }

  public void show()
  {
    fill(4, 154, 14);
    ellipse(posBallPlayerX, posBallPlayerY, sizeBallPlayer, sizeBallPlayer);
  }
  public void move()
  {
    posBallPlayerX += speedBallPlayerX;
    posBallPlayerY += speedBallPlayerY;
  }
  
  //destruction ball quand elle touche un mur ou rebond
  public void destruction()
  {
    if (whatsUnderneath(PApplet.parseInt(posBallPlayerX), PApplet.parseInt(posBallPlayerY)) != 0)
    {
      if (playerBallBounceCount == 0) ballPlayerWall = true;
      else
      {
        if (whatsUnderneath(PApplet.parseInt(posBallPlayerX+speedBallPlayer+(sizeBallPlayer)), PApplet.parseInt(posBallPlayerY)) != 0)
        {
          speedBallPlayerX = -speedBallPlayerX;
        }
        if (whatsUnderneath(PApplet.parseInt(posBallPlayerX-speedBallPlayer-(sizeBallPlayer)), PApplet.parseInt(posBallPlayerY)) != 0)
        {
          speedBallPlayerX = -speedBallPlayerX;
        }
        if (whatsUnderneath(PApplet.parseInt(posBallPlayerX), PApplet.parseInt(posBallPlayerY+speedBallPlayer+(sizeBallPlayer))) != 0)
        {
          speedBallPlayerY = -speedBallPlayerY;
        }
        if (whatsUnderneath(PApplet.parseInt(posBallPlayerX), PApplet.parseInt(posBallPlayerY-speedBallPlayer-(sizeBallPlayer))) != 0)
        {
          speedBallPlayerY = -speedBallPlayerY;
        }
        playerBallBounceCount -= 1;
      }
    }
    else ballPlayerWall = false;
  }
}

class BallEnnemie
{
  float posBallEnnemieX;
  float posBallEnnemieY;
  PVector VectoredspeedBallEnnemie = new PVector(speedBallEnnemie*cos(angleEnnemies), speedBallEnnemie*sin(angleEnnemies));  //calcul du vecteur de vitesse de la balle

  public BallEnnemie(int startX, int startY)
  {
    posBallEnnemieX=startX;
    posBallEnnemieY=startY;
  }

  public void show()
  {
    fill(255, 0, 0);
    ellipse(posBallEnnemieX, posBallEnnemieY, 10, 10);
  }
  
  public void move()
  {
    posBallEnnemieX+=VectoredspeedBallEnnemie.x;
    posBallEnnemieY+=VectoredspeedBallEnnemie.y;
  }
  
  //destruction balle quand elle touche un mur
  public void destruction()
  {
    if (whatsUnderneath(PApplet.parseInt(posBallEnnemieX), PApplet.parseInt(posBallEnnemieY)) != 0)
    {
      ballEnnemieWall = true;
    } else {
      ballEnnemieWall = false;
    }
  }
}
boolean inShop;
int delayClicks, priceBulletSize, priceBulletBounce, priceIncreaseHP, priceBulletDamage;

public void shop()
{
  if (whatsUnderneath(player1.posPlayerX, player1.posPlayerY) == 3 && Spitter.size() == 0)
  {
    fill(0,0,200);
    rect(width/2-70,height-270,140,50);
    fill(255);
    textSize(50);
    text("SHOP",width/2-65,height-225);
    if (mousePressed == true && mouseX >= width/2-70 && mouseX <= width/2+70 && mouseY >= height-270 && mouseY <= height-220)
    {
      inShop = true;
    }
    if (inShop == true)
    {
      //affichage de l'écran du shop
      fill(125,200);
      rect(width/4,height/4,width/2,width/2);
      
      //case augmentation taille balle joueur
      fill(255);
      textSize(25);
      text("Bullet size",width/4+40,height/4+210);
      text(priceBulletSize,width/4+80,height/4+235);
      fill(0xff00358c,200);
      rect(width/4+50,height/4+40,120,120);
      fill(4, 154, 14);
      ellipse(width/4+110, height/4+100, sizeBallPlayer, sizeBallPlayer);
      
      //case activation rebond
      fill(255);
      textSize(25);
      text("Bullet bounce",width/2+30,height/4+210);
      text(priceBulletBounce,width/2+80,height/4+235);
      if (playerBallBounce == false)
      {
        fill(0xff00358c,200);
        rect(width/2+50,height/4+40,120,120);
      }
      else
      {
        fill(0xff198c00,200);
        rect(width/2+50,height/4+40,120,120);
      }
      
      //case pour augmenter la vie max
      fill(255);
      textSize(25);
      text("Increase health",width/4+20,height/2+180);
      text(priceIncreaseHP,width/4+80,height/2+205);
      fill(0xff00358c,200);
      rect(width/4+50,height/2+20,120,120);
      
      //case augmentation dégâts
      fill(255);
      textSize(25);
      text("Bullet damage",width/2+10,height/2+180);
      text(priceBulletDamage,width/2+80,height/2+205);
      fill(0xff00358c,200);
      rect(width/2+50,height/2+20,120,120);
      fill(255);
      textSize(25);
      text(playerBallDamageMax,width/2+100,height/2+90);
      
      if (mousePressed == true && mouseX >= width/4+50 && mouseX <= width/4+170 && mouseY >= height/4+40 && mouseY <= height/4+160)
      {
        if (gold >= priceBulletSize)
        {
          delayClicks += 1;
          if (delayClicks == 1)
          {
            sizeBallPlayer += 1;
            gold -= priceBulletSize;
            priceBulletSize = PApplet.parseInt(priceBulletSize*1.1f);
          }
          if (delayClicks == 10)
          {
            delayClicks = 0;
          }
        }
      }
      
      else if (mousePressed == true && mouseX >= width/2+50 && mouseX <= width/2+170 && mouseY >= height/4+40 && mouseY <= height/4+160)
      {
        if (gold >= priceBulletBounce)
        {
          if (playerBallBounce == false)
          {
            playerBallBounce = true;
            gold -= priceBulletBounce;
          }
        }
      }
      
      else if (mousePressed == true && mouseX >= width/4+50 && mouseX <= width/4+170 && mouseY >= height/2+20 && mouseY <= height/2+140)
      {
        if (gold >= priceIncreaseHP)
        {
          if (max_HP < 16)
          {
            delayClicks += 1;
            if (delayClicks == 1)
            {
              max_HP += 2;
              hearts.append(1);
              gold -= priceIncreaseHP;
              player1.player_HP += 2;
              priceIncreaseHP = priceIncreaseHP*2;
            }
            if (delayClicks == 10)
            {
              delayClicks = 0;
            }
          }
        }
      }
      
      else if (mousePressed == true && mouseX >= width/2+50 && mouseX <= width/2+170 && mouseY >= height/2+20 && mouseY <= height/2+140)
      {
        if (gold >= priceBulletDamage)
        {
          delayClicks += 1;
          if (delayClicks == 1)
          {
            playerBallDamageMax += 1;
            gold -= priceBulletDamage;
            priceBulletDamage = PApplet.parseInt(priceBulletDamage*1.5f);
          }
          if (delayClicks == 10)
          {
            delayClicks = 0;
          }
        }
      }
      else delayClicks = 0;
    }
  }
  else
  {
    fill(125);
    rect((width/2)-70,height-270,140,50);
    fill(255);
    textSize(50);
    text("SHOP",(width/2)-65,height-225);
    inShop = false;
  }
}
  public void settings() {  size(910, 980);  smooth(); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "--present", "--window-color=#666666", "--stop-color=#cccccc", "main" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
