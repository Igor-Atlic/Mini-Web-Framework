package server;

import framework.annotations.Bean;
import framework.annotations.Qualifier;
import framework.request.enums.Beans;

@Qualifier(value = "Interfejs")
@Bean(scope = Beans.singleton)
public class Proba3 implements IProba{

    @Override
    public String str() {
        return "Implementacija interfejsa";
    }
}
