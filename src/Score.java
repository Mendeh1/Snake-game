// Clase que representa un puntaje con nombre y valor
class Score implements Comparable{
    // Variables para nombre y puntaje
    String name;
    int score;
    // Constructor y métodos de acceso
    public Score(String name, int score){
        this.name = name;
        this.score = score;
    }
    // Método de comparación para ordenar puntajes
    public String toString(){
        // Código para comparar puntajes
        return "name: " + this.name + " score: " + this.score;
    }

    public int getScore(){
        return score;
    }

    public String getName(){
        return name;
    }

    public int compareTo(Object o){
        Score b = (Score) o;
        return this.score - b.score;
    }
}