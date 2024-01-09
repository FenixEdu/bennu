<template>
  <div class="page">
    <header class="section-header page__header">
      <div class="section-header__text">
        <p class="h6 section-header__parent-page">
          <router-link :to="{ name: 'DomainBrowserPage' }">
            <span class="i-back-arrow" />
            <span>{{ $t('back') }}</span>
          </router-link>
        </p>
        <h1 class="section-header__title">
          {{ getDomainObjectName(domainObject) }} - {{ domainObject.objectId }} <span
            v-for="modifier in domainObject.modifiers"
            :key="modifier"
            class="label"
          >{{ modifier }}</span>
        </h1>
        <p>{{ domainObject.type }}</p>
      </div>
      <div class="section-header__meta">
        <button
          v-if="metadata.deletable"
          class="btn btn--danger"
          @click.prevent="isConfirmDeleteDomainObjectModalOpen = true"
        >
          {{ $t('actions.delete') }}
        </button>
      </div>
    </header>
    <tab-navigation
      v-model="currentTab"
      style="margin-top: 3.5rem;"
      :total-tabs="tabs.length"
      :label="$t('tabs.label')"
    >
      <template #tab-0="{ attrs, events }">
        <router-link
          :id="`tab-${tabs[0]}`"
          :to="{ name: tabs[0] }"
          replace
          v-bind="attrs"
          @click.native="events.click"
          @keydown.native="events.keydown"
        >
          {{ $t('tabs.slots') }}
        </router-link>
      </template>
      <template #tab-1="{ attrs, events }">
        <router-link
          :id="`tab-${tabs[1]}`"
          :to="{ name: tabs[1] }"
          replace
          v-bind="attrs"
          @click.native="events.click"
          @keydown.native="events.keydown"
        >
          {{ $t('tabs.roles') }}
        </router-link>
      </template>
      <template #tab-2="{ attrs, events }">
        <router-link
          :id="`tab-${tabs[2]}`"
          :to="{ name: tabs[2] }"
          replace
          v-bind="attrs"
          @click.native="events.click"
          @keydown.native="events.keydown"
        >
          {{ $t('tabs.role-sets') }}
        </router-link>
      </template>
    </tab-navigation>
    <router-view />
    <confirm-delete-domain-object-modal
      :domain-object="domainObject"
      :value="isConfirmDeleteDomainObjectModalOpen"
    />
  </div>
</template>

<script>
import TabNavigation from '@/components/utils/TabNavigation.vue'
import ConfirmDeleteDomainObjectModal from '@/components/domain-object/delete/ConfirmDeleteDomainObjectModal.vue'

export default {
  name: 'DomainObjectPage',
  components: {
    TabNavigation,
    ConfirmDeleteDomainObjectModal
  },
  beforeRouteUpdate: function (to, from, next) {
    this.currentTab = this.tabs.findIndex(route => route === to.name)
    next()
  },
  props: {
    domainObject: {
      type: Object,
      required: true
    },
    metadata: {
      type: Object,
      required: true
    }
  },
  data () {
    const tabs = ['DomainObjectSlotsTab', 'DomainObjectRolesTab', 'DomainObjectRoleSetsTab']
    return {
      tabs,
      currentTab: tabs.findIndex(route => route === this.$route.name),
      isConfirmDeleteDomainObjectModalOpen: false
    }
  },
  i18n: {
    messages: {
      pt: {
        back: 'Domain Browser',
        header: 'Navegador de domínios',
        description: 'Navegue pelos domínios do Fenix',
        tabs: {
          label: 'Detalhes do objeto de domínio',
          slots: 'Slots',
          roles: 'Roles',
          'role-sets': 'Role Sets'
        }
      },
      en: {
        back: 'Domain Browser',
        header: 'Domain browser',
        description: 'Browse Fenix domains',
        tabs: {
          label: 'Domain Object Details',
          slots: 'Slots',
          roles: 'Roles',
          'role-sets': 'Role Sets'
        }
      }
    }
  },
  methods: {
    getDomainObjectName (domainObject) {
      return domainObject.class.split('.').pop()
    },
    prettyJson (json) {
      return JSON.stringify(JSON.parse(json), null, 2)
    }
  }
}
</script>

<style lang="scss" scoped>
.object-label {
  margin-right: 0.5rem;
}

.slot-field {
  margin-top: 0;
  margin-bottom: 0;
}
</style>
