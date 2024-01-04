<template>
  <header
    v-click-outside="closeMobileMenu"
    class="main-header"
  >
    <div class="container container--header">
      <div class="main-header__logo">
        <router-link
          :to="{ name: 'LandingPage' }"
          :aria-label="$t('nav-bar.aria-label.homepage')"
          class="logo"
        >
          <svg>
            <g
              fill="none"
              fill-rule="evenodd"
            >
              <path
                d="M23.4116667 11.8098782h-1.272381v9.5279744h-2.7538095v-9.5279744h-1.2695238V9.8989231h5.2957143v1.9109551zm-6.7819048-4.9444808l-.0011905 17.5114359c0 2.2698077-1.8461904 3.4450321-4.1230952 3.4450321-2.2766667 0-4.1390476-.9918846-4.1390476-2.918968h.002381c0-.7563653.6159523-1.3701859 1.3747618-1.3701859.7588096 0 1.3742857.6138206 1.3742857 1.3701859h.0011905c0 1.3995962.1121429 2.3492629 1.3864286 2.3492629 1.37 0 1.37-.9539359 1.37-2.875327l.0011905-17.5114359c0-2.2679102 1.8454762-3.4440833 4.1228571-3.4440833 2.2766667 0 4.1390476.9906987 4.1390476 2.9187308h-.0021428c0 .7563654-.6154762 1.3697115-1.3754762 1.3697115-.7585714 0-1.3738095-.6133461-1.3738095-1.3697115h-.0016667c0-1.399359-.1114286-2.3490257-1.3859524-2.3490257-1.3704762 0-1.3697619.9522757-1.3697619 2.8743782zM8.3709524 9.8989231h2.7452381l.0040476 11.4320513H8.3680952l.0028572-11.4320513zM0 .1861859v15.450109C0 29.5478205 15.2492857 37 15.2492857 37s15.3064286-7.4521795 15.3064286-21.3637051V.1861859H0z"
                fill="#009DE0"
              />
              <text
                font-family="Klavika"
                font-size="17"
                font-weight="500"
                fill="#45555F"
              >
                <tspan
                  x="41"
                  y="21"
                >Bennu Admin</tspan>
              </text>
            </g>
          </svg>
        </router-link>
      </div>

      <!-- MOBILE TOP NAV BAR -->
      <ul
        class="mobile-nav mobile-only"
        hidden
      >
        <li>
          <button
            :aria-expanded="String(showMobileMenu)"
            class="mobile-nav-trigger"
            aria-controls="main-nav"
            :aria-label="$t('nav-bar.aria-label.menu')"
            @click.prevent="toggleMobileMenu"
          >
            <span class="icon icon-burger">
              <svg
                width="24"
                height="24"
                xmlns="http://www.w3.org/2000/svg"
              >
                <g
                  transform="translate(0 4)"
                  class="icon--fill"
                  fill="#2E3242"
                  fill-rule="evenodd"
                >
                  <g class="icon-burger__top-bar">
                    <rect
                      width="24"
                      height="2"
                      rx="1"
                    />
                  </g>
                  <g class="icon-burger__center-bar">
                    <rect
                      width="24"
                      height="2"
                      rx="1"
                      y="7"
                    />
                  </g>
                  <g class="icon-burger__bottom-bar">
                    <rect
                      width="24"
                      height="2"
                      rx="1"
                      y="14"
                    />
                  </g>
                </g>
              </svg>
            </span>
          </button>
        </li>
      </ul>
      <!-- END MOBILE TOP NAV BAR -->

      <nav
        id="main-nav"
        class="main-nav menu menu-active"
      >
        <!-- MOBILE SIDE MENU -->
        <mobile-side-menu
          v-if="isMobile"
          v-model="showMobileMenu"
          class="primary-nav"
          @closed="closeMobileMenu()"
        />
        <!-- END MOBILE SIDE MENU -->

        <template v-else>
          <!-- TODO: Review this and fix styles -->
          <ul>
            <li>
              <router-link
                :to="{ name: 'LandingPage' }"
                active-class="active"
                class="link-text"
              >
                {{ $t('nav-bar.menu.links.domain-browser') }}
              </router-link>
            </li>
          </ul>

          <ul class="utility-nav">
            <!-- DESKTOP TOP NAV BAR BUTTONS -->
            <li class="search">
              <button
                class="search-trigger"
                :aria-label="$t('menu.search.aria-label')"
                tag="a"
                @click.prevent="isSearchModalOpen = true"
              >
                <span class="icon icon-search">
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    width="19"
                    height="19"
                  >
                    <g
                      fill="none"
                      fill-rule="evenodd"
                      class="icon--stroke"
                      stroke="var(--gray400)"
                      stroke-width="2"
                    >
                      <ellipse
                        cx="8.261"
                        cy="8.741"
                        rx="6.938"
                        ry="6.972"
                        transform="matrix(-1 0 0 1 16.522 0)"
                      />
                      <path
                        stroke-linecap="round"
                        d="M14.016 13.58l3.9 3.56"
                      />
                    </g>
                  </svg>
                </span>
              </button>
            </li>
            <li class="languages">
              <dropdown
                size="md"
                class="dropdown--with-check"
              >
                <button
                  slot="dropdown-trigger"
                  :aria-label="$t('nav-bar.aria-label.language')"
                  class="lang-trigger"
                  aria-haspopup="true"
                >
                  <span class="icon icon-lang">
                    <svg
                      xmlns="http://www.w3.org/2000/svg"
                      width="20"
                      height="16"
                    >
                      <path
                        fill="#8F95A1"
                        class="icon--fill"
                        fill-rule="nonzero"
                        d="M9.399 9.399c-1.202-1.231-2.404-2.35-2.95-4.923h4.48V2.573h-4.48V0H4.48v2.573H0v1.903h4.48c0 .67.11.335 0 .67-.655 2.462-1.42 4.14-4.48 5.707l.656 1.902c2.95-1.566 4.48-3.468 5.136-5.706.656 1.678 1.749 3.02 2.951 4.252L9.4 9.399zM15.3 3.133h-2.514L8.306 16h1.967l1.312-3.804h5.136L18.033 16H20L15.3 3.133zm-3.17 7.049l1.967-5.147 1.968 5.147H12.13z"
                      />
                    </svg>
                  </span>
                  <span class="icon-label">
                    {{ $t('nav-bar.aria-label.language') }}
                  </span>
                </button>
                <div slot="dropdown-panel">
                  <ul class="dropdown-menu">
                    <li class="dropdown-menu__item">
                      <button
                        :class="{ 'dropdown-menu__link--selected': $i18n.locale === 'pt' }"
                        class="dropdown-menu__link"
                        @click.prevent="setLocale('pt', $auth)"
                      >
                        <span class="link-text"> Português </span>
                      </button>
                    </li>
                    <li class="dropdown-menu__item">
                      <button
                        :class="{ 'dropdown-menu__link--selected': $i18n.locale === 'en' }"
                        class="dropdown-menu__link"
                        @click.prevent="setLocale('en', $auth)"
                      >
                        <span class="link-text"> English </span>
                      </button>
                    </li>
                  </ul>
                </div>
              </dropdown>
            </li>

            <li
              v-if="profile"
              class="user"
            >
              <user-dropdown-menu />
            </li>
            <li
              v-else
              class="login"
            >
              <router-link
                :to="{ name: 'LoginPage' }"
                class="btn--login"
              >
                {{ $t('nav-bar.menu.links.login') }}
              </router-link>
            </li>
          </ul>
        </template>
      </nav>
    </div>
    <search-modal v-model="isSearchModalOpen" />
  </header>
</template>

<script>
import { mapState } from 'vuex'
import ClickOutside from 'vue-click-outside'
import Dropdown from '@/components/utils/Dropdown.vue'
import UserDropdownMenu from '@/components/nav-menus/UserDropdownMenu.vue'
import MobileSideMenu from '@/components/nav-menus/MobileSideMenu.vue'
import SearchModal from '@/components/SearchModal.vue'

export default {
  name: 'TopNavBar',
  components: {
    Dropdown,
    UserDropdownMenu,
    MobileSideMenu,
    SearchModal
  },
  directives: {
    ClickOutside
  },
  data () {
    return {
      isSearchModalOpen: false,
      mobileMenuBreakpoint: 1200,
      showMobileMenu: false,
      windowWidth: 0,
      isMobile: false
    }
  },
  computed: {
    ...mapState({
      profile: (state) => state.profile
    })
  },
  watch: {
    windowWidth: {
      immediate: true,
      handler (newWidth, oldWidth) {
        if (newWidth < this.mobileMenuBreakpoint) {
          this.isMobile = true
        } else {
          this.closeMobileMenu()
          this.isMobile = false
        }
      }
    }
  },
  mounted () {
    this.$nextTick(function () {
      window.addEventListener('resize', this.getWindowWidth)
      this.getWindowWidth()
    })

    window.addEventListener('keydown', this.onCtlrK)
  },
  beforeDestroy () {
    window.removeEventListener('resize', this.getWindowWidth)
    window.removeEventListener('keydown', this.onCtlrK)
  },
  methods: {
    getWindowWidth () {
      this.windowWidth = window.innerWidth
    },
    closeMobileMenu () {
      this.showMobileMenu = false
      document.body.classList.remove('scrollLock')
      this.$emit('toggle-mobile-menu', this.showMobileMenu)
    },
    openMobileMenu () {
      this.showMobileMenu = true
      document.body.classList.add('scrollLock')
      this.$emit('toggle-mobile-menu', this.showMobileMenu)
    },
    onCtlrK (e) {
      if (e.key === 'k' && e.ctrlKey) {
        this.isSearchModalOpen = true
      }
    },
    toggleMobileMenu () {
      if (this.showMobileMenu) {
        this.closeMobileMenu()
      } else {
        this.openMobileMenu()
      }
    }
  }
}
</script>
<style lang="scss">
@import "@/assets/scss/variables";
@import "@/assets/scss/helpers";

// ================== Main Header Styles
.main-header,
.container--header {
  display: flex;
  flex-direction: row;
  align-items: stretch;
  justify-content: space-between;
}

.main-header {
  background: rgba(#fff, 0.97);
  width: 100%;
  height: $header-height;
  box-shadow: 0 0.0625rem 0 0 rgba($dark, 0.1);
  position: fixed;
  z-index: 2;
  // padding: 0.25rem 0;
}

.main-header__logo {
  display: flex;
  flex-direction: row;
  align-items: center;
  padding: 0.5rem 0;
}

.logo {
  display: flex;
  flex-direction: row;
  align-items: center;
  max-width: 12.5rem;
  height: 100%;
  height: 2rem;
  max-height: 100%;
}

.logo img,
.logo svg {
  width: auto;
  height: 100%;
  object-fit: contain;
}

.main-header__logo .logo svg.ul {
  width: 100%;
  height: auto;
}

.mobile-nav {
  display: flex;
  align-items: center;
}

.mobile-nav li ~ li {
  margin-left: 1.5rem;
}

// ================== Global Menu Styles
.menu .moves-out {
  transform: translateX(-100%);
}

.menu > ul ul {
  position: absolute;
  top: 0;
  left: 100%;
}

.menu .has-children > a::before,
.menu .has-children > button::before,
.menu .go-back > a::before,
.menu .go-back > button::before {
  position: absolute;
  content: "";
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
}

.menu .has-children > a::after,
.menu .has-children > button::after,
.menu .go-back > button::after,
.menu .go-back > a::after {
  position: absolute;
  content: "";
  top: 50%;
  height: 0.625rem;
  width: 0.625rem;
  border: 0.125rem solid $slate;
  border-left: 0;
  border-bottom: 0;
  transform: rotate(45deg) translateY(-50%);
}

.menu .has-children > button::after,
.menu .has-children > a::after {
  right: 1rem;
}

.menu .go-back > button,
.menu .go-back > a {
  padding-left: 2.5rem;
}

.menu .go-back > button::after,
.menu .go-back > a::after {
  left: 1rem;
  transform: rotate(-135deg) translateY(50%);
}

// a editar
.menu .is-hidden {
  opacity: 0;
  visibility: hidden;
  pointer-events: none;
}

// IF CSS is disabled, visibility of .mobile-only elements is turned off with attribute "hidden" to simplify document outline,
// but if CSS is enabled they are needed
.mobile-only[hidden] {
  display: flex;

  @media screen and (min-width: 75rem) {
    display: none;
  }
}

// ================== end Global Menu Styles

// ================== Main Nav on mobile
.main-nav {
  position: fixed;
  top: 0;
  bottom: 0;
  right: -#{$menu-mobile--width};
  width: $menu-mobile--width;
  height: 100vh;
  background-color: $dark-600;
  overflow-x: hidden;
  overflow-y: auto;
}

.main-nav ul {
  display: flex;
  flex-direction: column;
  width: 100%;
  transition-property: all;

  @include fastTransition;
}

.primary-nav button,
.primary-nav a {
  position: relative;
  color: #fff;
  display: flex;
  padding: 0 16px;
  width: 100%;
  height: $header-height;
  line-height: $header-height;
  border-bottom: 0.0625rem solid $dark;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

//mobile active state
.primary-nav li .active {
  color: $blue;
  font-weight: 600;
}
// ================== end of Main Nav on mobile

// ================== Main Nav on desktop
@media screen and (min-width: 75rem) {
  // ================== Global Menu Styles
  .menu > ul ul {
    position: static;
  }

  .menu a {
    color: $dark;
    display: block;
    font-size: 1rem;
    transition-property: color;

    @include mdTransition;

    &:hover,
    &:focus {
      color: $blue-600;
    }

    &:active {
      color: $blue-700;
    }
  }
  // ================== end Global Menu Styles

  .main-nav {
    position: static;
    width: calc(100% - 23rem);
    height: auto;
    background-color: #fff;
    display: flex;
    align-items: stretch;
    justify-content: space-between;
    margin-left: auto;
    transition-property: none;
    overflow: visible;
  }

  // .main-nav ul,
  .main-nav > ul {
    flex-direction: row;
    width: auto;
  }

  .main-nav > ul > li {
    display: flex;
  }

  .main-nav > ul > li > a {
    display: flex;
    align-items: center;
  }

  // TODO: Review this.
  .main-nav > ul > li:not(:first-of-type) > a {
    margin-left: 2rem;
  }

  .main-nav a {
    height: auto;
    line-height: normal;
    white-space: nowrap;
    white-space: normal;
    padding: 0;
    border-bottom: none;

    &.active {
      color: $blue;

      &:hover,
      &:focus {
        color: $blue-600;
        border-color: $blue-600;
      }
    }
  }

  .main-nav .has-children > a::after,
  .main-nav .has-children > button::after,
  .main-nav .go-back > button::after,
  .main-nav .go-back > a::after {
    content: initial;
  }

  .main-nav .has-children.has-focus > a,
  .main-nav .has-children:focus-within > a {
    color: $blue-600;
  }

  .main-nav .secondary-nav {
    background-color: $light-blue;
    padding: 2.5rem calc((100vw - 1110px) / 2);
    position: absolute;
    left: 0;
    right: 0;
    top: $header-height;
    width: 100%;
    height: auto;
    display: flex;
    flex-direction: row;
    justify-content: flex-start;
    align-items: flex-start;
    transition-property: opacity, visibility;

    @include mdTransition;

    transition-delay: 0.18s;
  }

  .primary-nav > li ~ li {
    margin-left: 2rem;
  }

  .primary-nav > li.has-children:focus-within .is-hidden {
    opacity: 1;
    visibility: visible;
    pointer-events: all;
  }

  // focus-within not widely supported yet
  .primary-nav > li.has-children:focus-within > a {
    color: $blue-600;
    box-shadow: inset 0 -0.125rem 0 $blue-600;
  }

  // alternative to focus-within -> add class .has-focus via js
  .primary-nav > li.has-children:hover .is-hidden,
  .primary-nav > li.has-children.has-focus .is-hidden {
    opacity: 1;
    visibility: visible;
    pointer-events: all;
  }

  .primary-nav > li.has-children > button,
  .primary-nav > li.has-children > a {
    position: relative;
    box-shadow: inset 0 -0.125rem 0 transparent;
    transition-property: color, box-shadow;

    @include mdTransition;

    &:hover,
    &:focus {
      box-shadow: inset 0 -0.125rem 0 $blue-600;
    }

    &:active {
      box-shadow: inset 0 -0.125rem 0 $blue-700;
    }
  }

  .primary-nav > li.has-children.has-focus > button,
  .primary-nav > li.has-children.has-focus > a {
    color: $blue-600;
    box-shadow: inset 0 -0.125rem 0 $blue-600;

    &:active {
      color: $blue-700;
      box-shadow: inset 0 -0.125rem 0 $blue-700;
    }
  }

  p.has-image {
    font-weight: 400;
    margin-top: 1rem;
  }

  p.has-image img {
    margin: 0.5rem 16px 0 0;
  }

  .secondary-nav ul {
    flex-direction: column;
  }

  .secondary-nav .intro-message {
    display: block;
    opacity: 0.65;
    font-weight: 400;
    line-height: 1.5;
    text-align: left;
    margin-top: 1rem;
    transition-property: opacity;

    @include mdTransition;
  }

  .secondary-nav > li {
    flex-basis: 23%;
    margin-right: 2%;
    overflow: hidden;
  }

  .secondary-nav > li > a {
    font-weight: 600;
    margin-bottom: 0.5rem;
  }

  .secondary-nav > li ul li {
    margin-top: 0.625rem;
  }

  .secondary-nav .intro a:hover p,
  .secondary-nav .intro a:focus p,
  .secondary-nav .intro a:hover span,
  .secondary-nav .intro a:focus span {
    opacity: 1;
  }

  .utility-nav {
    align-items: center;
    margin-left: auto;
  }

  .utility-nav > li ~ li {
    margin-left: 1rem;
  }

  .utility-nav > li > a {
    font-size: 0.875rem;
  }
}

// ================== Main Header icons
.main-header .utility-nav .icon {
  display: none;

  @media screen and (min-width: 75rem) {
    display: flex;

    & + .icon-label {
      @include sr-only;
    }
  }
}

.notifications {
  position: relative;
}

.notifications__count {
  position: absolute;
  top: 0;
  right: -2px;
  content: "";
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background-color: $blue-600;

  // to animate (?)
  opacity: 0;
  transform: scale(0.1);
  transition-property: transform;

  @include mdTransition;
}

.notifications__count.is-visible {
  opacity: 1;
  transform: scale(1);
}

.search-trigger,
.notifications-trigger {
  &:hover,
  &:focus {
    .icon--stroke {
      stroke: $blue-600;
    }
  }

  &:active {
    .icon--stroke {
      stroke: $blue-700;
    }
  }
}

.apps-trigger,
.lang-trigger,
.mobile-nav-trigger {
  &:hover,
  &:focus {
    .icon--fill {
      fill: $blue-600;
    }
  }

  &:active {
    .icon--fill {
      fill: $blue-700;
    }
  }
}

.icon-burger {
  .icon-burger__top-bar,
  .icon-burger__center-bar,
  .icon-burger__bottom-bar {
    transition-property: transform, transform-origin, opacity;

    @include mdTransition;
  }

  .nav-is-visible & {
    .icon-burger__top-bar {
      transform-origin: top left;
      transform: rotate(45deg) translate(3px, -4px);
    }

    .icon-burger__center-bar {
      opacity: 0;
    }

    .icon-burger__bottom-bar {
      transform-origin: bottom left;
      transform: rotate(-45deg) translate(8px, 7px);
    }
  }
}

.icon-lang,
.icon-apps,
.icon-burger {
  .icon--fill {
    transition-property: fill;

    @include mdTransition;
  }
}

.icon-search,
.icon-notifications {
  .icon--stroke {
    transition-property: stroke;

    @include mdTransition;
  }
}

.main-header .icon {
  width: 1.5rem;
  height: 1.5rem;
  display: flex;
  align-items: center;
  justify-content: center;
}

// Avatars

// user avatar on main-header
.user-container {
  width: 2.25rem;
  height: 2.25rem;
  display: flex;
  align-items: center;
  justify-content: center;
}

// user avatar on main-header
.user-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  border: 2px solid rgb(0 0 0 / 12%);
  transition-property: border;

  .user-trigger.has-focus &,
  .user-trigger:hover &,
  .user-trigger:focus & {
    border: 2px solid rgba($blue-600, 1);
  }

  .user-trigger:active & {
    border: 2px solid rgba($blue-700, 1);
  }
}

.user-avatar.user-avatar--lg {
  width: 48px;
  height: 48px;
}
</style>
