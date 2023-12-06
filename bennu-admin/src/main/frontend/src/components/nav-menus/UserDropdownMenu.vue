<template>
  <dropdown
    size="lg"
    @hide="closeMenu"
  >
    <button
      slot="dropdown-trigger"
      :aria-label="$t('nav-bar.aria-label.account')"
      class="user-trigger"
      aria-haspopup="true"
    >
      <avatar
        :name="profile.displayName || 'User'"
        :src="profile.avatarUrl"
        class="figure--36"
      />
    </button>
    <div
      ref="dropdownPanel"
      slot="dropdown-panel"
      class="user-dropdown-menu__panel"
    >
      <ul
        ref="menu"
        class="dropdown-menu"
      >
        <li class="dropdown-user__details">
          <avatar
            :name="profile.displayName || ''"
            :src="profile.avatarUrl"
            class="figure--48"
          />
          <div class="dropdown-user__text">
            <div
              v-if="profile.displayName"
              class="name"
            >
              {{ profile.displayName }}
            </div>
            <div
              v-if="profile.username || profile.email"
              class="user-name"
            >
              {{ profile.username || profile.email }}
            </div>
          </div>
        </li>
        <li class="dropdown-menu__item">
          <button
            class="dropdown-menu__link"
            @click.stop="$auth.logout()"
          >
            <span
              class="link-icon"
              aria-hidden="true"
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                width="18"
                height="19"
              >
                <g
                  fill="none"
                  fill-rule="evenodd"
                >
                  <path d="M-6-3h24v24H-6z" />
                  <g
                    stroke="#8F95A1"
                    stroke-linecap="round"
                  >
                    <path
                      stroke-linejoin="round"
                      stroke-width="2"
                      d="M9.076923 1.5h-8V18h7.5750767"
                    />
                    <path
                      stroke-width="1.8"
                      d="M12 6l4 4.0122369L12.0256659 14M6 10h9"
                    />
                  </g>
                </g>
              </svg>
            </span>
            <span class="link-text">
              {{ $t('nav-bar.menu.links.logout') }}
            </span>
          </button>
        </li>
      </ul>
    </div>
  </dropdown>
</template>

<script>
import { mapState } from 'vuex'
import Dropdown from '@/components/utils/Dropdown.vue'
import Avatar from '@/components/utils/Avatar.vue'

export default {
  components: {
    Dropdown,
    Avatar
  },
  computed: {
    ...mapState({
      profile: state => state.profile
    })
  },
  methods: {
    openSubMenu (event) {
      const selectedElement = event.target.parentNode
      const subMenuElement = selectedElement.querySelector('ul')
      const parentNav = selectedElement.parentNode
      // adjust height of nav to selected content
      const dropdownPanel = this.$refs.dropdownPanel.parentNode
      dropdownPanel.style.height = `${subMenuElement.offsetHeight}px`

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

      // adjust height of nav to selected content
      const dropdownPanel = this.$refs.dropdownPanel.parentNode
      dropdownPanel.style.height = `${previousNav.offsetHeight}px`

      currentNav.classList.add('is-hidden')
      currentNav.parentNode.classList.remove('selected')
      previousNav.classList.remove('moves-out')
    },
    closeMenu () {
      const menu = this.$refs.menu
      if (!menu) return
      const navigations = menu.querySelectorAll('.has-children ul')
      const selectedNavigations = menu.querySelectorAll('.has-children button')
      const elementsMovedOut = menu.querySelectorAll('.moves-out')

      for (const navigation of navigations) {
        navigation.classList.add('is-hidden')
      }
      for (const selectedNavigation of selectedNavigations) {
        selectedNavigation.classList.remove('selected')
      }
      for (const elementMovedOut of elementsMovedOut) {
        elementMovedOut.classList.remove('moves-out')
      }

      setTimeout(() => {
      // reset height of nav to automatic detection
        const dropdownPanel = this.$refs.dropdownPanel.parentNode
        dropdownPanel.setAttribute('style', '')
      }, 100)

      menu.classList.remove('moves-out')
    }
  }
}
</script>

<style lang="scss" scoped>
@import "@/assets/scss/variables";
// TODO: move styles from TopNavBar to this component?

.link-icon--component {
  width: 48px;
  margin-top: -14px;
  margin-bottom: -14px;
}

.user-dropdown-menu__panel {
  overflow: hidden;
}

// FIXME: remove !important when menu styles are moved from TopNavBar to menu components
.dropdown-menu__secondary-nav {
  position: absolute !important;
  top: 0 !important;
  left: 100% !important;
  width: 100% !important;
}

.dropdown-menu__item .link-text {
  // disables clicks on the spans, bc we want clicks to fire on the buttons
  pointer-events: none;
}

.dropdown-menu__secondary-nav > .dropdown-menu__item a {
  padding-left: 2.5rem;
}

.dropdown-menu .has-children > button::after,
.dropdown-menu .go-back > button::after {
  position: absolute;
  content: "";
  top: 50%;
  height: 0.625rem;
  width: 0.625rem;
  border: 0.125rem solid $gray-400;
}

.dropdown-menu .has-children > button::after {
  border-left: 0;
  border-bottom: 0;
  right: 2rem;
  transform: rotate(45deg) translateY(-50%);
}

.dropdown-menu .go-back > button::after {
  border-right: 0;
  border-top: 0;
  left: 1.25rem;
  transform: rotate(45deg) translateY(-50%);
}
</style>
