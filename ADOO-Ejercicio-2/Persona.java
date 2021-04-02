public class Persona
{
    public class Tio extends Persona
    {
        int funcion1 ( )
        {
            if (1 == 1);
            
            if (2 == 2);

            if (3 == 3);

            if (3 == 3);
            
            if (4 == 4);

            if (5 == 5);

            if (5 == 5);

            return 0;
        }

        void duerme ()
        {
            return;
        }
    }

    public class Estudiante extends Persona
    {
        void estudiar ( )
        {
            return;
        }
    }

    public class Carlos extends Tio
    {
        
    }

    public class Sofia extends Estudiante
    {

    }

    public class Mesme extends Estudiante
    {

    }
}

class ClaseFuera extends Persona 
{
    public ClaseFuera ( )
    {
        if (1 == 1);
    }

    public void hola ( )
    {
        if (1 == 1);
    }

    class Anidada1 extends Carlos
    {
        class Anidada2 extends Anidada1
        {
            class Anidada3 extends Anidada2
            {
                class Anidada4 extends Anidada3
                {
                    class Anidada5 extends Anidada4
                    {
                        int numero5 ( )
                        {
                            return 0;
                        }
                    }

                    int numero4 ( )
                    {
                        return 0;
                    }
                }

                int numero3 ( )
                {
                    return 0;
                }
            }

            int numero2 ( )
            {
                return 0;
            }
        }
        int numero1 ( )
        {
            return 0;
        }
    }

}