package transferinterfaces;

import base.RuleSet;

import java.util.Locale;

public interface RuleSetExporter {
    String getDescription(Locale usedLocale);
    void setup();
    RuleSet exportRuleSet();
}
