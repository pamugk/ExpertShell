package transferinterfaces;

import base.RuleSet;

import java.util.Locale;

public interface RuleSetImporter {
    String getDescription(Locale usedLocale);
    void setup();
    RuleSet importRuleSet();
}
