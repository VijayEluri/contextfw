package net.contextfw.benchmark;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.contextfw.web.application.remote.ResourceResponse;

public class ProductImageResponder implements ResourceResponse {

    private final String name;
    
    public ProductImageResponder(String name) {
        this.name = name;
    }

    @Override
    public void serve(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        BufferedImage bufferedImage = new BufferedImage(40, 40, BufferedImage.TYPE_INT_RGB);
      //Draw an oval
        Graphics g = bufferedImage.getGraphics();
        g.setColor(Color.white);
        g.fillRect(0, 0, 50, 50);
        g.setColor(Color.pink);
        g.fillOval(0, 0, 39,39);
        g.setFont(new Font("SansSerif", Font.PLAIN, 11));
        g.setColor(Color.black);
        g.drawString(name, 10, 24);
        g.dispose();
        response.setContentType("image/jpeg");
        ImageIO.write(bufferedImage, "jpg", response.getOutputStream());  
    }
}
