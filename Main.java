package battleship;
import java.io.IOException;
import java.util.Scanner;

//战场
class Battlefield {
    char[][] a = new char[11][11]; //战场(自我视角)
    int[][] b = new int[12][12]; //指示不可放置船只的区域
    char[][] c = new char[11][11]; //迷雾战场(对手视角)

    void createField() {
        for (int i = 0; i < 11; i++) {
            for ( int j = 0; j < 11; j++) {
                if (i != 0 && j != 0) {
                    a[i][j] = '~';
                    b[i][j] = 1;
                    c[i][j] = '~';
                }
                else if (i == 0) { //首行
                    a[i][j] = (char)( '0' + j); //a[0][0] = '0' not ' ' ; a[0][10] = ':' not '10'
                    c[i][j] = (char)( '0' + j);
                }
                else { //首列
                    a[i][j] = (char)( '@' + i); //'@' + 1 = 'A'
                    c[i][j] = (char)( '@' + i);
                }
            }
        }
    }

    void showBattleField() {
        System.out.println("  1 2 3 4 5 6 7 8 9 10"); //首行,实际首行为{'0','1','2','3','4','5','6','7','8','9',':'}
        for (int i = 1; i < 11; i++) { //从第二行输出
            for (int j = 0; j < 11; j++) {
                System.out.print(a[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    void showFogField() {
        System.out.println("  1 2 3 4 5 6 7 8 9 10"); //首行,实际首行为{'0','1','2','3','4','5','6','7','8','9',':'}
        for (int i = 1; i < 11; i++) { //从第二行输出
            for (int j = 0; j < 11; j++) {
                System.out.print(c[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

}



//船
class Ship {
    int len;
    String type;
    int x1,x2,y1,y2;
    int stance; //0代表水平，1代表垂直,2代表倾斜
    boolean coordinate;
    int status; //1代表船只存活，即半损或未损，0代表船只沉没，即全损

    Ship() {
        len = 0;
        type = "ship";
        status = 1;
    }

    //设置坐标
    void setCoordinate(String str1, String str2) {
        coordinate = parse(str1, str2);
    }

    //解析坐标
    boolean parse(String str1, String str2) {
        boolean flag1 = false, flag2 = false;
        if (str1.length() == 2) {
            if (str1.charAt(0) >= 'A' && str1.charAt(0) <= 'J' && str1.charAt(1) >= '1' && str1.charAt(1) <= '9') {
                x1 = str1.charAt(0) - '@';
                y1 = str1.charAt(1) - '0';
                flag1 = true;
            }
        }

        if (str1.length() == 3) {
            if (str1.charAt(0) >= 'A' && str1.charAt(0) <= 'J' && str1.charAt(1) == '1' && str1.charAt(2) == '0') {
                x1 = str1.charAt(0) - '@';
                y1 = 10;
                flag1 = true;
            }
        }

        if (str2.length() == 2) {
            if (str2.charAt(0) >= 'A' && str2.charAt(0) <= 'J' && str2.charAt(1) >= '1' && str2.charAt(1) <= '9') {
                x2 = str2.charAt(0) - '@';
                y2 = str2.charAt(1) - '0';
                flag2 = true;
            }
        }

        if (str2.length() == 3) {
            if (str2.charAt(0) >= 'A' && str2.charAt(0) <= 'J' && str2.charAt(1) == '1' && str2.charAt(2) == '0') {
                x2 = str2.charAt(0) - '@';
                y2 = 10;
                flag2 = true;
            }
        }

        if (flag1 && flag2) {
            if (x1 == x2) {
                stance = 0;
            }else if (y1 == y2) {
                stance = 1;
            }else
                stance = 2;
            return true;
        }else
            return false;
    }

    //在战场上放置一艘船
    boolean layout(Battlefield bf) {
        //检查船只坐标是否正确
        if (!coordinate) {
            System.out.println("Error! Wrong String format");
            return false;
        }
        //检查船只是否倾斜
        if (stance == 2) {
            System.out.println("Error! Wrong ship location! Try again:");
            return false;
        }

        //检查船只长度是否合适
        if (!checkLength()) {
            System.out.println("Error! Wrong length of the "+ this.type +"! Try again:");
            return false;
        }

        //检查船只是否接触
        if (!checkTouch(bf)) {
            System.out.println("Error! You placed it too close to another one. Try again:");
            return false;
        }

        //lay
        if (stance == 0) { //水平船
            int i = x1;
            for (int j = Math.min(y1, y2); j < Math.min(y1, y2) + len; j++)
                bf.a[i][j] = 'O';
            restrictedArea(bf);
            return true;
        }

        if (stance == 1) { //垂直船
            int j = y1;
            for (int i = Math.min(x1, x2); i < Math.min(x1, x2) + len; i++)
                bf.a[i][j] = 'O';
            restrictedArea(bf);
            return true;
        }
        return false;
    }

    //检查船只长度是否合适
    boolean checkLength() {
        if (stance == 0 && Math.abs(y1 - y2) + 1 != len) {
                return false;
        }else if (stance == 1 && Math.abs(x1 - x2) + 1 != len) {
                return false;
        }else {
            return  true;
        }
    }

    //检查船只是否接触
    boolean checkTouch(Battlefield bf) {
        if (stance == 0) { //水平船
            int i = x1;
            for (int j = Math.min(y1, y2); j < Math.min(y1, y2) + len; j++) {
                if (bf.b[i][j] == 0)
                    return false;
            }
        }

        if (stance == 1) { //垂直船
            int j = y1;
            for (int i = Math.min(x1, x2); i < Math.min(x1, x2) + len; i++) {
                if (bf.b[i][j] == 0)
                    return false;
            }
        }
        return true;
    }

    //设置禁区
    void restrictedArea( Battlefield bf) {
        if (stance == 0) { //水平船
            for (int i = x1 - 1; i <= x1 +1; i++) {
                for (int j = Math.min(y1, y2) - 1; j < Math.min(y1, y2) + len + 1; j++) {
                    bf.b[i][j] = 0;
                }
            }
        }

        if (stance == 1) { //垂直船
            for (int j = y1 - 1; j <= x1 +1; j++) {
                for (int i = Math.min(x1, x2) - 1; i < Math.min(x1, x2) + len + 1; i++) {
                    bf.b[i][j] = 0;
                }
            }
        }

    }

    //船只自检,返回true代表未受损或半损伤状态，返回false代表全毁坏
    boolean selfCheck(Battlefield bf) {
        if (stance == 0) { //水平船
            int i = x1;
            for (int j = Math.min(y1, y2); j < Math.min(y1, y2) + len; j++)
                if (bf.c[i][j] != 'X') {
                    return true;
                }
        }

        if (stance == 1) { //垂直船
            int j = y1;
            for (int i = Math.min(x1, x2); i < Math.min(x1, x2) + len; i++)
                if (bf.c[i][j] != 'X') {
                    return true;
                }
        }

        return false;
    }

}



//航空母舰
class AircraftCarrier extends Ship {
    AircraftCarrier() {
        super();
        len = 5;
        type = "AircraftCarrier";
    }
}
//战舰
class Battleship extends Ship {
    Battleship() {
        super();
        len = 4;
        type = "Battleship";
    }
}
//潜艇
class Submarine extends Ship {
    Submarine () {
        super();
        len = 3;
        type = "Submarine";
    }
}
//巡洋舰
class Cruiser extends Ship {
    Cruiser() {
        super();
        len = 3;
        type = "Cruiser";
    }
}
//驱逐舰
class Destroyer extends Ship {
    Destroyer() {
        super();
        len = 2;
        type = "Destroyer";
    }
}



//炮弹
class Bomb {
    int x, y;

    //解析坐标
    boolean parse1(String str) {

        if (str.length() == 2) {
            if (str.charAt(0) >= 'A' && str.charAt(0) <= 'J' && str.charAt(1) >= '1' && str.charAt(1) <= '9') {
                x = str.charAt(0) - '@';
                y = str.charAt(1) - '0';
                return true;
            }
        }

        if (str.length() == 3) {
            if (str.charAt(0) >= 'A' && str.charAt(0) <= 'J' && str.charAt(1) == '1' && str.charAt(2) == '0') {
                x = str.charAt(0) - '@';
                y = 10;
                return true;
            }
        }

        return false;
    }

    //开炮!
    boolean takeShot(Battlefield bf) {
        Scanner sc = new Scanner(System.in);
        String str = sc.next();
        if (parse1(str)) {
            //checkHit();
            if (bf.a[x][y] == 'O' || bf.a[x][y] == 'X' ) {
                bf.a[x][y] = 'X';
                bf.c[x][y] = 'X';
                System.out.println("You hit a ship!");
            }else {
                bf.a[x][y] = 'M';
                bf.c[x][y] = 'M';
                System.out.println("You missed!");
            }
            return true;
        }else {
            return false;
        }

    }
}



//玩家
class player {
    Battlefield bf = new Battlefield();

    AircraftCarrier ac = new AircraftCarrier();
    Battleship bs = new Battleship();
    Submarine sm = new Submarine();
    Cruiser ci = new Cruiser();
    Destroyer dt = new Destroyer();

    Scanner sc = new Scanner(System.in);

    //布置船只
    void takePosition() {
        while(true) {
            System.out.println("Enter the coordinates of the Aircraft Carrier (5 cells):");
            String str1 = sc.next();
            String str2 = sc.next();
            ac.setCoordinate(str1, str2);
            if (ac.layout(bf)) {
                bf.showBattleField();
                break;
            }
        }

        while(true) {
            System.out.println("Enter the coordinates of the Battleship (4 cells):\n");
            String str1 = sc.next();
            String str2 = sc.next();
            bs.setCoordinate(str1, str2);
            if (bs.layout(bf)) {
                bf.showBattleField();
                break;
            }
        }

        while(true) {
            System.out.println("Enter the coordinates of the Submarine (3 cells):");
            String str1 = sc.next();
            String str2 = sc.next();
            sm.setCoordinate(str1, str2);
            if (sm.layout(bf)) {
                bf.showBattleField();
                break;
            }
        }

        while(true) {
            System.out.println("Enter the coordinates of the Cruiser (3 cells):");
            String str1 = sc.next();
            String str2 = sc.next();
            ci.setCoordinate(str1, str2);
            if (ci.layout(bf)) {
                bf.showBattleField();
                break;
            }
        }

        while(true) {
            System.out.println("Enter the coordinates of the Destroyer (2 cells):");
            String str1 = sc.next();
            String str2 = sc.next();
            dt.setCoordinate(str1, str2);
            if (dt.layout(bf)) {
                bf.showBattleField();
                break;
            }
        }

    }

    //交战
    boolean battle(player p) {
        Bomb bo = new Bomb();
        while(!bo.takeShot(p.bf)) {
            System.out.println("Error! You entered the wrong coordinates! Try again:");
        }
        return battleSituation(p);
    }

    //检查战况,true代表敌方的船只全被消灭，战争结束，false代表还有剩余的船只，战争还未结束
    boolean battleSituation(player p) {
        if (p.ac.status == 1 && !p.ac.selfCheck(p.bf)) {
            System.out.println("You sank a ship! Specify a new target:");
            p.ac.status = 0;
        }

        if (p.bs.status == 1 && !p.bs.selfCheck(p.bf)) {
            System.out.println("You sank a ship! Specify a new target:");
            p.bs.status =0;
        }

        if (p.sm.status == 1 && !p.sm.selfCheck(p.bf)) {
            System.out.println("You sank a ship! Specify a new target:");
            p.sm.status = 0;
        }

        if (p.ci.status == 1 && !p.ci.selfCheck(p.bf)) {
            System.out.println("You sank a ship! Specify a new target:");
            p.ci.status = 0;
        }

        if (p.dt.status == 1 && !p.dt.selfCheck(p.bf)) {
            System.out.println("You sank a ship! Specify a new target:");
            p.dt.status = 0;
        }

        if (p.ac.status == 0 && p.bs.status == 0 && p.sm.status == 0 && p.ci.status == 0 && p.dt.status == 0) {
            System.out.println("You sank the last ship. You won. Congratulations!");
            return true;
        }
        return false;
    }

}




public class Main {

    //for Enter press :
    public static void promptEnterKey() {
        System.out.println("Press Enter and pass the move to another player");
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        //创建玩家
        player p1 = new player();
        player p2 = new player();

        //双方玩家分别布置船只
        System.out.println("Player 1, place your ships on the game field\n");
        p1.bf.createField();
        p1.bf.showBattleField();
        p1.takePosition();
        promptEnterKey();

        System.out.println("Player 2, place your ships on the game field\n");
        p2.bf.createField();
        p2.bf.showBattleField();
        p2.takePosition();
        promptEnterKey();

        //双方玩家轮流进行攻击
        int flag = 1;
        while(true) {
            if (flag == 1) {
                p2.bf.showFogField();
                System.out.println("---------------------");
                p1.bf.showBattleField();
                System.out.println("Player 1, it's your turn:");
                if (p1.battle(p2))
                    break;
                flag = 2;
            }else {
                p1.bf.showFogField();
                System.out.println("---------------------");
                p2.bf.showBattleField();
                System.out.println("Player 2, it's your turn:");
                if (p2.battle(p1))
                    break;
                flag = 1;
            }

            promptEnterKey();
        }

        System.out.println("GAME OVER!");
    }
}

/*
F3 F7
A1 D1
J7 J10
J10 J8
B9 D8
B9 D9
E6 D6
I2 J2

F3 F7 A1 D1 J10 J8 B9 D9 I2 J2

  1 2 3 4 5 6 7 8 9 10
A O ~ ~ ~ ~ ~ ~ ~ ~ ~
B O ~ ~ ~ ~ ~ ~ ~ O ~
C O ~ ~ ~ ~ ~ ~ ~ O ~
D O ~ ~ ~ ~ ~ ~ ~ O ~
E ~ ~ ~ ~ ~ ~ ~ ~ ~ ~
F ~ ~ O O O O O ~ ~ ~
G ~ ~ ~ ~ ~ ~ ~ ~ ~ ~
H ~ ~ ~ ~ ~ ~ ~ ~ ~ ~
I ~ O ~ ~ ~ ~ ~ ~ ~ ~
J ~ O ~ ~ ~ ~ ~ O O O

A1 B1 C1 D1 F3 F4 F5 F6 F7 B9 C9 D9 I2 J2 J8 J9 J10


H2 H6 F3 F6 D4 D6 F8 H8 C8 D8

  1 2 3 4 5 6 7 8 9 10
A ~ ~ ~ ~ ~ ~ ~ ~ ~ ~
B ~ ~ ~ ~ ~ ~ ~ ~ ~ ~
C ~ ~ ~ ~ ~ ~ ~ O ~ ~
D ~ ~ ~ O O O ~ O ~ ~
E ~ ~ ~ ~ ~ ~ ~ ~ ~ ~
F ~ ~ O O O O ~ O ~ ~
G ~ ~ ~ ~ ~ ~ ~ O ~ ~
H ~ O O O O O ~ O ~ ~
I ~ ~ ~ ~ ~ ~ ~ ~ ~ ~
J ~ ~ ~ ~ ~ ~ ~ ~ ~ ~

F3 F7 A1 D1 J10 J8 B9 D9 I2 J2
H2 H6 F3 F6 D4 D6 F8 H8 C8 D8
*/
