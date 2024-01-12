package org.fenixedu.bennuAdmin.util;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import org.fenixedu.bennu.BennuAdminConfiguration;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.commons.i18n.LocalizedString;

public interface LocalizedStringBuilder {

  Supplier<Set<Locale>> locales =
      () ->
          new HashSet<Locale>() {
            {
              add(Locale.forLanguageTag("pt-PT"));
              add(Locale.forLanguageTag("en-GB"));
            }
          };

  BiFunction<String, String, LocalizedString> ls =
      (final String pt, final String en) ->
          new LocalizedString(Locale.forLanguageTag("pt-PT"), pt)
              .with(Locale.forLanguageTag("en-GB"), en);

  default LocalizedString bundleLs(final String key) {
    return BundleUtil.getLocalizedString(BennuAdminConfiguration.BUNDLE, key);
  }

  default LocalizedString bundleLs(final String key, final String... args) {
    return BundleUtil.getLocalizedString(BennuAdminConfiguration.BUNDLE, key, args);
  }

  default LocalizedString bundleLs(final String key, final LocalizedString... args) {
    return BundleUtil.getLocalizedString(BennuAdminConfiguration.BUNDLE, key, args);
  }

  default LocalizedString ls(final String pt, final String en) {
    return ls.apply(pt, en);
  }

  default LocalizedString ls(final String str) {
    return ls.apply(str, str);
  }

  default LocalizedString ls(Function<Locale, String> fn) {
    final LocalizedString.Builder localizedString = new LocalizedString.Builder();
    locales.get().forEach(locale -> localizedString.with(locale, fn.apply(locale)));
    return localizedString.build();
  }
}
