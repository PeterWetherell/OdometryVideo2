public class Fresnel {
    private static final double[] S_COEFFICIENTS = new double[]{
            0.3333333333333333333333333333333333333333,
            -0.0238095238095238095238095238095238095239,
            0.0007575757575757575757575757575757575757,
            -0.0000132275132275132275132275132275132276,
            1.450385222315046876450385222315046E-7,
            -1.0892221037148573380457438428453E-9,
            5.9477940136376350368119915445E-12,
            -2.46682701026445692771004258E-14,
            8.03273501241577360913984E-17,
            -2.107855191442135824861E-19,
            -5.4728704520104326582E-21
    };

    private static final double S_CUTOFF = 2.2538;

    private static double taylorS(double x){
        double out = 0;
        for (int i = 0; i < S_COEFFICIENTS.length; i++) {
            out += S_COEFFICIENTS[i]*Math.pow(x, 4*i+3);
        }
        return out;
    }

    private static double riemannS(double x){
        double u = x*x;

        //solve sin(u)/u+2cos(u)=0

        double u0 = u-((u-Math.PI/2)%Math.PI);//pretty close

        for (int i = 0; i < 3; i++) {
            double dfdv = (-1/u0/u0-2)*Math.sin(u0)+Math.cos(u0)/u0;
            double f = Math.sin(u0)/u0+2*Math.cos(u0);
            u0 -= f/dfdv;//newtons method
        }
        double integral = 0.5*Math.sqrt(Math.PI/2); //S(v)=1/2*sqrt(pi/2) (known value)
        double x0 = Math.sqrt(u0);
        double dx = .01f;

        double prev = Math.sin(x0*x0);
        while(x0+dx<x){
            x0 += dx;
            double curr = Math.sin(x0*x0);
            integral += (curr+prev)/2*dx;
            prev = curr;
        }
        double curr = Math.sin(x*x);
        dx = x-x0;
        integral += (curr+prev)/2*dx;
        return integral;
    }

    public static double S(double x){
        double absx = Math.abs(x);
        if(absx<S_CUTOFF){
            return taylorS(x);
        }
        return (x<0?-1:1)*riemannS(absx);
    }

    public static void main(String[] args) {
        for (double i = 0; i < 100; i += 0.1) {
            System.out.printf("(%f,%f)%n", (double)i, S(i));
            //S(i);
        }
    }
}