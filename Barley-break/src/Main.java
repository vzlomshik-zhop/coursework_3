//"Релизная" версия с комментариями

import java.awt.*;
import java.awt.event.*;
import java.math.*;
import java.util.*;

class Main {
    public static void main(String[] argv) throws Exception {
        new MyFrame("Pyatnashki");
    }
}

class StartButton extends Button implements ActionListener, MouseListener {
    MyFrame win;
    boolean f;

    public StartButton(String title, MyFrame win) {
        super(title);
        this.win = win;
        f = true;
        setFocusable(false);
        setSize(400, 100);
        setLocation(0, 23);
        setBackground(new Color(200, 200, 200));
        addActionListener(this);
        addMouseListener(this);
    }

    public void changeLabel(String s) {
        setLabel(getLabel() + s);
    }

    public void actionPerformed(ActionEvent ae) {
        win.setSize(400, 523);
        win.setRandomOrder(f);
        if (f)
            f = false;
        setLabel("Refresh");
        new WinSeeker(win).start();
        setLocation(0, 423);
    }

    public void mouseEntered(MouseEvent me) {
        setBackground(new Color(180, 180, 180));
    }

    public void mouseClicked(MouseEvent me) {}

    public void mouseExited(MouseEvent me) {
        setBackground(new Color(200, 200, 200));
    }

    public void mousePressed(MouseEvent me) {}

    public void mouseReleased(MouseEvent me) {}
}

class NumButton extends Button implements ActionListener, MouseListener {
    MyFrame win;
    MyThread mt;
    int x, y;
    int ind;

    public NumButton(int ind, int x, int y, MyFrame win) {
        super(Integer.toString(ind));
        this.ind = ind;
        this.win = win;
        this.x = x;
        this.y = y;
        setFocusable(false);
        int wx = x - 1, hy = y - 1;
        setSize(100, 100);
        setLocation(wx * 100, hy * 100 + 24);
        setBackground(new Color(200, 200, 200));
        addActionListener(this);
        addMouseListener(this);
    }

    public int getIndex() {
        return ind;
    }

    public void actionPerformed(ActionEvent ae) {
        Point p = win.getDrainPoint();
        //Проверка,не является ли сосед "дыркой"
        if ((Math.abs(x - p.x) == 1 && Math.abs(y - p.y) == 0) || (Math.abs(x - p.x) == 0 && Math.abs(y - p.y) == 1)) {
            //Блоки(try - catch) для устранения конфликта потоков
            try {
                if (!mt.isAlive()) {
                    int ty = y, tx = x;
                    //Установка новой дырки
                    win.setDrainPoint(new Point(x, y));
                    //Замена координат в полях объекта(x2)
                    win.replaceCoordiantes(win.bt[p.y][p.x], x, y);
                    win.replaceCoordiantes(win.bt[y][x], p.x, p.y);
                    //Замена ссылок в двухмерном массиве кнопок
                    win.replaceLinks(p.y, p.x, ty, tx);
                    //Перемещение дырки на место текущего элемента
                    win.bt[ty][tx].setLocation((tx - 1) * 100, (ty - 1) * 100 + 24);
                    //Плавное перемещение в потоке
                    mt = new MyThread(win, p.x, p.y, tx, ty);
                    mt.start();
                }
            } catch (Exception ex) {
                //То же самое, что и выше
                int ty = y, tx = x;
                win.setDrainPoint(new Point(x, y));
                win.replaceCoordiantes(win.bt[p.y][p.x], x, y);
                win.replaceCoordiantes(win.bt[y][x], p.x, p.y);
                win.replaceLinks(p.y, p.x, ty, tx);
                win.bt[ty][tx].setLocation((tx - 1) * 100, (ty - 1) * 100 + 24);
                mt = new MyThread(win, p.x, p.y, tx, ty);
                mt.start();
            }
        }
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setIndex(int i) {
        this.ind = i;
    }

    public void mouseEntered(MouseEvent me) {
        setBackground(new Color(180, 180, 180));
    }

    public void mouseClicked(MouseEvent me) {}

    public void mouseExited(MouseEvent me) {
        setBackground(new Color(200, 200, 200));
    }

    public void mousePressed(MouseEvent me) {}

    public void mouseReleased(MouseEvent me) {}
}

class MyFrame extends Frame implements WindowListener {
    NumButton bt[][];
    StartButton sb;
    int dx, dy;
    boolean isItFirstTime;

    public MyFrame(String title) {
        super(title);
        bt = new NumButton[5][5];
        setLayout(null);
        isItFirstTime = true;
        setSize(400, 123);
        setLocation(600, 250);
        setBackground(new Color(0, 0, 0));
        addWindowListener(this);
        add(sb = new StartButton("Start the game", this));
        setVisible(true);
    }

    //Рандомное заполнение массива
    public void setRandomOrder(boolean f) {
        if (f & !isItFirstTime) {
            for (int i = 0; i <= 4; i++)
                for (int j = 0; j <= 4; j++)
                    remove(bt[i][j]);
            isItFirstTime = false;
        }
        int b[] = new int[17];
        int x = 0, y = 0;
        //Заполнение массива [1 .. n] числами 1 .. n
        for (int i = 1; i <= 16; i++)
            b[i] = i;
        int n = 16;
        //Выбор рандомного элемента t из n индексов, удаление элемента t из массива и выбор из последующих n - k элементов, где k - воличество проходов
        while (n != 0) {
            //Выбор
            int t = 1 + (int)(Math.random() * (n - 1));
            x = (b[t] - 1) % 4 + 1;
            y = (b[t] - 1) / 4 + 1;
            n--;
            //Добавление кнопки
            //Если вызывается с f = true - создает новые кнопки, с f = false - перерисовывает кнопки
            if (f)
                add(bt[y][x] = new NumButton(16 - n, x, y, this));
            else {
                bt[y][x].setVisible(true);
                bt[y][x].setSize(100, 100);
                bt[y][x].setIndex(16 - n);
                bt[y][x].setX(x);
                bt[y][x].setY(y);
                bt[y][x].setLocation((x - 1) * 100, (y - 1) * 100 + 24);
                bt[y][x].setLabel(Integer.toString(bt[y][x].getIndex()));
            }
            //Удаление
            for (int i = t; i <= n; i++)
                b[i] = b[i + 1];
        }
        //Выбор "дырки"
        bt[y][x].setSize(0, 0);
        setDrainPoint(new Point(x, y));
    }

    public Point getDrainPoint() {
        Point p = new Point(dx, dy);
        return p;
    }

    public void setDrainPoint(Point p) {
        dx = p.x;
        dy = p.y;
    }

    public void replaceCoordiantes(NumButton btn, int x, int y) {
        btn.setX(x);
        btn.setY(y);
    }

    public void replaceLinks(int i, int j, int i1, int j1) {
        NumButton t = bt[i][j];
        bt[i][j] = bt[i1][j1];
        bt[i1][j1] = t;
    }

    public void windowClosing(WindowEvent we) {
        System.exit(0);
    }

    public void windowClosed(WindowEvent we) {}

    public void windowOpened(WindowEvent we) {}

    public void windowActivated(WindowEvent we) {}

    public void windowDeactivated(WindowEvent we) {}

    public void windowIconified(WindowEvent we) {}

    public void windowDeiconified(WindowEvent we) {}
}

class MyThread extends Thread {
    MyFrame win;
    int trgx, trgy;
    int lx, ly;
    int i, j;

    public MyThread(MyFrame win, int trgx, int trgy, int lx, int ly) {
        this.win = win;
        this.trgx = trgx;
        this.trgy = trgy;
        this.lx = lx;
        this.ly = ly;
        this.i = trgy;
        this.j = trgx;
    }

    public void run() {
        int nx, ny;
        int x, y;
        //Выбор начальной точки и конечной(в пикселях)
        x = (lx - 1) * 100;
        y = (ly - 1) * 100;
        nx = (trgx - 1) * 100;
        ny = (trgy - 1) * 100;
        int kx = 0, ky = 0;
        //Выбор направления
        if ((nx - x) < 0)
            kx = -1;
        if ((nx - x) > 0)
            kx = 1;
        if ((nx - x) == 0)
            kx = 0;
        if ((ny - y) < 0)
            ky = -1;
        if ((ny - y) > 0)
            ky = 1;
        if ((ny - y) == 0)
            ky = 0;
        //Перемещение кнопки по 1 пикселю по направлению kx(где значение "-1" - перемещение влево, "1" - вправо, "0" - оставаться на месте) и ky(где значение "-1" - перемещение вверх, "1" - вниз, "0" - оставаться на месте)
        while (x != nx || y != ny) {
            x += kx;
            y += ky;
            win.bt[i][j].setLocation(x, y + 24);
            try {
                Thread.sleep(3);
            } catch (Exception e) {}
        }
    }
}

class WinSeeker extends Thread {
    MyFrame win;

    public WinSeeker(MyFrame win) {
        this.win = win;
    }

    public void run() {
        boolean f = false;
        while (!f) {
            if (win.bt[4][4].getIndex() == 16) {
                int a = 0;
                int t = 0;
                for (int i = 1; i <= 4 & a == t; i++) {
                    a++;
                    t = win.bt[i][1].getIndex();
                    for (int j = 2; j <= 4 & a == t; j++) {
                        a++;
                        t = win.bt[i][j].getIndex();
                    }
                }
                if (a == 16) {
                    f = true;
                    win.setSize(400, 123);
                    win.sb.setSize(400, 100);
                    win.sb.setLocation(0, 23);
                    win.sb.setLabel("You won! Play again?");
                    for (int i = 1; i <= 4; i++)
                        for (int j = 1; j <= 4; j++)
                            win.bt[i][j].setVisible(false);
                }
            }
            try {
                Thread.sleep(200);
            } catch (Exception e) {}
        }
    }
}
