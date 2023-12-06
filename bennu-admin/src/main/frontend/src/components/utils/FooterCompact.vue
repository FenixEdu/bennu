<template>
  <footer
    :class="{'footer-compact--compact-line': compactLine}"
    class="footer-compact"
  >
    <ul class="footer-links">
      <li>
        <a
          :href="$t('footer.links.about-tecnico.url')"
          target="_blank"
          rel="noopener noreferrer"
        >
          {{ $t('footer.links.about-tecnico.label') }}
        </a>
      </li>
      <li>
        <a
          :href="$t('footer.links.general-contacts.url')"
          target="_blank"
          rel="noopener noreferrer"
        >
          {{ $t('footer.links.general-contacts.label') }}
        </a>
      </li>
      <li>
        <a
          :href="$t('footer.links.terms-and-conditions.url')"
          target="_blank"
          rel="noopener noreferrer"
        >
          {{ $t('footer.links.terms-and-conditions.label') }}
        </a>
      </li>
    </ul>
    <p class="footer-copyrights">
      {{ new Date().getFullYear() }} ©
      <a
        href="//tecnico.ulisboa.pt"
        target="_blank"
        rel="noopener noreferrer"
      >
        Instituto Superior Técnico</a>,
      <a
        href="//ulisboa.pt"
        target="_blank"
        rel="noopener noreferrer"
      >
        Universidade de Lisboa
      </a>
    </p>
  </footer>
</template>

<script>
export default {
  name: 'FooterCompact',
  props: {
    compactLine: {
      type: Boolean,
      default: false
    }
  },
  i18n: {
    messages: {
      pt: {
        footer: {
          links: {
            'about-tecnico': {
              label: 'Sobre o Técnico',
              url: '//tecnico.ulisboa.pt/pt/sobre-o-tecnico/'
            },
            'general-contacts': {
              label: 'Contactos Gerais',
              url: '//tecnico.ulisboa.pt/pt/contactos/'
            },
            'terms-and-conditions': {
              label: 'Termos e Condições',
              url: '//tecnico.ulisboa.pt/pt/informacoes/termos-e-condicoes/'
            }
          }
        }
      },
      en: {
        footer: {
          links: {
            'about-tecnico': {
              label: 'About Técnico',
              url: '//tecnico.ulisboa.pt/en/about-tecnico/'
            },
            'general-contacts': {
              label: 'General Contacts',
              url: '//tecnico.ulisboa.pt/en/contacts/'
            },
            'terms-and-conditions': {
              label: 'Terms and Conditions',
              url: '//tecnico.ulisboa.pt/en/informations/terms-conditions/'
            }
          }
        }
      }
    }
  }
}
</script>

<style lang="scss" scoped>
@import "@/assets/scss/variables";
.footer-compact {
  align-self: center;
  padding: 2rem 1rem;
  flex: 1;
  max-width: 72.375rem;
  border-top: 1px solid $light-gray;
  display: flex;
  flex-flow: column nowrap;
  align-items: center;
  &.footer-compact--compact-line {
    max-width: 52.375rem;
  }
  .footer-links,
  .footer-copyrights {
    font-size: 0.875rem;
  }
  .footer-copyrights {
    margin-top: 1rem;
    text-align: center;
  }
  .footer-links {
    display: flex;
    flex-flow: column nowrap;
    align-items: center;
    li {
      margin: 0.5rem 0;
    }
    a:link,
    a:visited {
      color: $dark;
    }
  }
  @media screen and (min-width: 75rem) {
    padding: 2rem 0;
  }

  @media screen and (min-width: 54.375rem) {
    flex-flow: row wrap;
    justify-content: space-between;
    .footer-copyrights {
      margin-top: 0;
    }
    .footer-links {
      flex-flow: row nowrap;
      li {
        margin-right: 2rem;
      }
    }
  }
}
</style>
