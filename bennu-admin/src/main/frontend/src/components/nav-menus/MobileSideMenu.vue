<template>
  <ul
    ref="menu"
    class="mobile-side-menu"
  >
    <template v-if="profile">
      <li
        hidden
        class="mobile-only mobile-side-menu__user"
      >
        <div class="link-icon">
          <avatar
            :src="profile.avatarUrl"
            :name="profile.displayName || ''"
            class="user-avatar user-avatar--lg"
          />
        </div>
        <div class="mobile-side-menu__username">
          <div class="link-text ellipsize">
            {{ profile.displayName }}
          </div>
          <div
            v-if="profile.username || profile.email"
            class="link-text link-text--username ellipsize"
          >
            {{ profile.username || profile.email }}
          </div>
        </div>
      </li>
      <li
        hidden
        class="mobile-only"
      >
        <router-link
          :to="{ name: 'DomainBrowserPage' }"
          @click.native="closeMenu()"
        >
          <span class="link-text">
            {{ $t('nav-bar.menu.links.domain-browser') }}
          </span>
        </router-link>
      </li>
      <li
        hidden
        class="mobile-only"
      >
        <button @click.stop="$auth.logout()">
          <span class="link-text">
            {{ $t('nav-bar.menu.links.logout') }}
          </span>
        </button>
      </li>
    </template>
    <li
      hidden
      class="has-children mobile-only language-menu"
      :class="{ 'menu-entry--snap-to-bottom': !!profile }"
    >
      <button
        aria-controls="language-menu"
        @click.prevent="openSubMenu"
      >
        <span class="link-text">
          {{ $t('nav-bar.menu.links.language') }}
        </span>
      </button>
      <ul
        id="language-menu"
        class="secondary-nav is-hidden"
      >
        <li class="go-back mobile-only">
          <button
            aria-controls="language-menu"
            @click.prevent="closeSubMenu"
          >
            <span class="link-text">
              {{ $t('nav-bar.menu.actions.back') }}
            </span>
          </button>
        </li>
        <li>
          <button
            v-if="$i18n.locale === 'en'"
            @click.prevent="closeMenu() + setLocale('pt', $auth)"
          >
            <span class="link-text">
              Português
            </span>
          </button>
        </li>
        <li>
          <button
            v-if="$i18n.locale === 'pt'"
            @click.prevent="closeMenu() + setLocale('en', $auth)"
          >
            <span class="link-text">
              English
            </span>
          </button>
        </li>
      </ul>
    </li>
    <li
      v-if="!profile"
      hidden
      class="login mobile-only menu-entry--snap-to-bottom"
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

<script>
import { mapState } from 'vuex'
import Avatar from '@/components/utils/Avatar.vue'

export default {
  components: {
    Avatar
  },
  props: {
    value: {
      type: Boolean,
      required: true
    }
  },
  computed: {
    ...mapState({
      profile: state => state.profile
    })
  },
  watch: {
    value (isOpen) {
      if (!isOpen) {
        this.closeMenu()
      }
    }
  },
  methods: {
    openSubMenu (event) {
      const selectedElement = event.target.parentNode
      const subMenuElement = selectedElement.querySelector('ul')
      const parentNav = selectedElement.parentNode
      if (subMenuElement.classList.contains('is-hidden')) {
        selectedElement.classList.add('selected')
        subMenuElement.classList.remove('is-hidden')
        parentNav.classList.add('moves-out')
      } else {
        selectedElement.classList.remove('selected')
        parentNav.classList.add('moves-out')
      }
    },
    closeSubMenu (event) {
      const selectedElement = event.target.parentNode
      const currentNav = selectedElement.parentNode
      const previousNav = currentNav.parentNode.parentNode
      currentNav.classList.add('is-hidden')
      currentNav.parentNode.classList.remove('selected')
      previousNav.classList.remove('moves-out')
    },
    closeMenu () {
      const mobileMenu = this.$refs.menu
      if (!mobileMenu) return
      const navigations = mobileMenu.querySelectorAll('.has-children ul')
      const selectedNavigations = mobileMenu.querySelectorAll('.has-children a')
      const elementsMovedOut = [mobileMenu, ...mobileMenu.querySelectorAll('.moves-out')]

      for (const navigation of navigations) {
        navigation.classList.add('is-hidden')
      }
      for (const selectedNavigation of selectedNavigations) {
        selectedNavigation.classList.remove('selected')
      }
      for (const elementMovedOut of elementsMovedOut) {
        elementMovedOut.classList.remove('moves-out')
      }
      this.$emit('closed')
    }
  }
}
</script>

<style lang="scss" scoped>
@import "@/assets/scss/variables";
// TODO: move styles from TopNavBar to this component?

.mobile-side-menu {
  height: 100%;
  display: flex;
  flex-flow: column nowrap;
}

.menu-entry--snap-to-bottom {
  margin-top: auto;

  &.language-menu {
    border-top: 0.0625rem solid $dark;
  }
}

.mobile-side-menu__user {
  position: relative;
  color: #fff;
  display: flex;
  padding: 1rem;
  width: 100%;
  min-height: calc(2rem + #{$header-height});
  border-bottom: 0.0625rem solid $dark;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;

  .mobile-side-menu__username {
    display: flex;
    flex-direction: column;
    justify-content: center;
  }

  .link-icon {
    margin-right: 1rem;
  }

  .link-text--username {
    color: $gray-400;
  }

  .user-avatar {
    border: none;
  }

  .ellipsize {
    max-width: 11rem;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}
</style>
