import java.util.*;
import java.io.*;

// ================== Player Class ==================
class Player {
    private String name;
    private int hp;
    private int score;

    public Player(String name) {
        this.name = name;
        this.hp = 20;  // starting HP
        this.score = 0;
    }

    public String getName() { return name; }
    public int getHp() { return hp; }
    public int getScore() { return score; }

    public void takeDamage(int damage) {
        hp -= damage;
        if (hp < 0) hp = 0;
    }

    public void addScore(int points) {
        score += points;
    }
}

// ================== Abstract Enemy Class ==================
abstract class Enemy {
    protected String name;
    protected int hp;

    public Enemy(String name, int hp) {
        this.name = name;
        this.hp = hp;
    }

    public String getName() { return name; }
    public int getHp() { return hp; }
    public void takeDamage(int dmg) {
        hp -= dmg;
        if (hp < 0) hp = 0;
    }

    public abstract boolean isWeakTo(String attackType);

    public int counterAttack() {
        Random rand = new Random();
        return rand.nextInt(4) + 3; // random damage 3–6
    }
}

// ================== Slime Enemy ==================
class SlimeEnemy extends Enemy {
    public SlimeEnemy() {
        super("Slime", 15);
    }

    @Override
    public boolean isWeakTo(String attackType) {
        return attackType.equals("Modulus Strike") && hp % 3 == 0;
    }
}

// ================== Goblin Enemy ==================
class GoblinEnemy extends Enemy {
    public GoblinEnemy() {
        super("Goblin", 17);
    }

    private boolean isPrime(int n) {
        if (n <= 1) return false;
        for (int i = 2; i <= Math.sqrt(n); i++) {
            if (n % i == 0) return false;
        }
        return true;
    }

    @Override
    public boolean isWeakTo(String attackType) {
        return attackType.equals("Prime Strike") && isPrime(hp);
    }
}

// ================== Attack Types ==================
class AttackType {
    public static final String BASIC = "Basic Strike";
    public static final String PRIME = "Prime Strike";
    public static final String MODULUS = "Modulus Strike";
}

// ================== Main Game ==================
    class RPGGame {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // === Player Name Validation ===
        String name;
        while (true) {
            System.out.print("Enter your name (letters only): ");
            name = sc.nextLine();
            if (name.matches("[a-zA-Z]+")) break;
            System.out.println("Invalid name! Letters only.");
        }

        Player player = new Player(name);

        // === Random Enemy Selection ===
        Random rand = new Random();
        Enemy enemy;
        if (rand.nextBoolean()) {
            enemy = new SlimeEnemy();
        } else {
            enemy = new GoblinEnemy();  
        }

        System.out.println("\nA wild " + enemy.getName() + " appears with " + enemy.getHp() + " HP!");
        System.out.println(player.getName() + " starts with " + player.getHp() + " HP.");

        // === Available Attacks ===
        String[] attacks = {AttackType.BASIC, AttackType.PRIME, AttackType.MODULUS};

        // === Game Loop ===
        while (player.getHp() > 0 && enemy.getHp() > 0) {
            System.out.println("\nChoose your attack:");
            for (int i = 0; i < attacks.length; i++) {
                System.out.println((i + 1) + ". " + attacks[i]);
            }

            int choice;
            try {
                choice = Integer.parseInt(sc.nextLine());
            } catch (Exception e) {
                System.out.println("Invalid choice, try again!");
                continue;
            }

            if (choice < 1 || choice > attacks.length) {
                System.out.println("Invalid choice, try again!");
                continue;
            }

            String attackChosen = attacks[choice - 1];
            boolean effective = false;

            if (attackChosen.equals(AttackType.BASIC)) {
                effective = true;
            } else {
                effective = enemy.isWeakTo(attackChosen);
            }

            if (effective) {
                enemy.takeDamage(5); // deal damage
                player.addScore(10);
                System.out.println("Your " + attackChosen + " was effective! Enemy HP is now " + enemy.getHp());
            } else {
                System.out.println("Your " + attackChosen + " had no effect!");
            }

            if (enemy.getHp() > 0) {
                int dmg = enemy.counterAttack();
                player.takeDamage(dmg);
                System.out.println(enemy.getName() + " counterattacks and deals " + dmg + " damage! Your HP: " + player.getHp());
            }
        }

        // === Game Results ===
        if (enemy.getHp() <= 0) {
            System.out.println("\nYou defeated the " + enemy.getName() + "!");
            System.out.println("Final Score: " + player.getScore());
        } else {
            System.out.println("\nGame Over! You were defeated by the " + enemy.getName());
        }

        // === Save Score to File ===
        saveScore(player);
    }

    public static void saveScore(Player player) {
        try (FileWriter fw = new FileWriter("scores.txt", true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {

            out.println(player.getName() + " - Score: " + player.getScore());
            System.out.println("Score saved to scores.txt!");

        } catch (IOException e) {
            System.out.println("Error saving score.");
        }
    }
}
