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
          {{ getDomainObjectName(domainObject) }} <span
            v-for="modifier in domainObject.modifiers"
            :key="modifier"
            class="label"
          >{{ modifier }}</span>
        </h1>
        <p><span class="label label--sm label--light label--outline object-label">{{ domainObject.oid }}</span> {{ domainObject.type }}</p>
      </div>
      <div class="section-header__meta">
        <form
          role="search"
          class="f-search f-search--sm"
        >
          <input
            ref="searchInput"
            v-model="searchInput"
            :placeholder="$t('search.placeholder')"
            class="f-search__input f-search__input--card"
            type="text"
            @keydown.enter.prevent="performSearch()"
          >
          <button
            class="f-search__submit"
            @click.prevent="performSearch()"
          >
            {{ $t('search.aria-label') }}
          </button>
        </form>
      </div>
    </header>
    <div
      v-if="domainObject.slots.length > 0"
      class="card"
    >
      <div class="card-row">
        <div class="card-row__text">
          <h2 class="card-row__title h3">
            {{ $t('domain-object.slots') }}
          </h2>
        </div>
      </div>
      <div
        v-for="slot in domainObject.slots"
        :key="slot.name"
        class="card-row card-row--sm"
      >
        <div class="card-row__text">
          <text-area
            v-if="slot.value"
            class="slot-field"
            variant="outlined"
            :name="`${slot.name}-value`"
            :label="slot.name"
            :description="slot.type"
            :value="slot.value"
          />
          <template v-else>
            <p>
              <span class="h5 h5--ssp">{{ slot.name }}</span> - {{ slot.type }} <span
                v-for="modifier in slot.modifiers"
                :key="modifier"
                class="label label--sm object-label label--outline label--light"
              >{{ modifier }}</span>
            </p>
          </template>
        </div>
      </div>
    </div>
    <div
      v-if="domainObject.relationSlots.length > 0"
      class="card"
    >
      <div class="card-row">
        <div class="card-row__text">
          <h2 class="card-row__title h3">
            {{ $t('domain-object.relation-slots') }}
          </h2>
        </div>
      </div>
      <div
        v-for="slot in domainObject.relationSlots"
        :key="slot.value"
        class="card-row card-row--sm"
      >
        <div class="card-row__text">
          <p>
            <span class="h4 h4--ssp">{{ slot.name }}</span> <span
              v-if="slot.value"
              class="label label--outline label--light"
            >{{ slot.value }}</span>
          </p>
          <p>{{ slot.type }}</p>
        </div>
        <div
          v-if="slot.value"
          class="card-row__meta"
        >
          <router-link
            aria-hidden="true"
            tabindex="-1"
            :to="{ name: 'DomainObjectPage', params: { domainObjectId: slot.value }}"
          >
            <span class="i-arrow-right" />
          </router-link>
        </div>
      </div>
    </div>
    <div
      v-if="domainObject.relationSets && domainObject.relationSets.length > 0"
      class="card"
    >
      <div class="card-row">
        <div class="card-row__text">
          <h2 class="card-row__title h3">
            {{ $t('domain-object.relation-sets') }}
          </h2>
        </div>
      </div>
      <div
        v-for="relationSet in domainObject.relationSets"
        :key="relationSet.name"
        class="card-row card-row--sm"
      >
        <div class="card-row__text">
          <p>
            <span class="h5 h5--ssp">{{ relationSet.name }}</span> - {{ relationSet.type }}
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { guardWithErrorHandling } from '@/router/guards'
import TextArea from '@/components/utils/fields/TextArea.vue'

export default {
  name: 'DomainObjectPage',
  components: {
    TextArea
  },
  beforeRouteUpdate: guardWithErrorHandling(async function (to, from, next) {
    this.$progress.set(10)
    await to.meta.beforeLoad(to, from)
    this.searchInput = to.meta.domainObject.oid
    this.$progress.complete()
    next()
  }),
  props: {
    domainObject: {
      type: Object,
      required: true
    }
  },
  data () {
    return {
      searchInput: this.domainObject.oid
    }
  },
  i18n: {
    messages: {
      pt: {
        back: 'Domain Browser',
        header: 'Navegador de domínios',
        description: 'Navegue pelos domínios do Fenix',
        search: {
          'aria-label': 'Pesquisar...',
          placeholder: 'Pesquisar domínios',
          empty: 'Nenhum domínio encontrado',
          'domain-object': {
            slots: 'Slots',
            'relation-slots': 'Slots de relação',
            'relation-sets': 'Conjuntos de relação'
          }
        }
      },
      en: {
        back: 'Domain Browser',
        header: 'Domain browser',
        description: 'Browse Fenix domains',
        search: {
          'aria-label': 'Search...',
          placeholder: 'Search domains',
          empty: 'No domains found'
        },
        'domain-object': {
          slots: 'Slots',
          'relation-slots': 'Relation slots',
          'relation-sets': 'Relation sets'
        }
      }
    }
  },
  methods: {
    getDomainObjectName (domainObject) {
      return domainObject.type.split('.').pop()
    },
    async performSearch () {
      if (this.searchInput !== this.$route.query.q) {
        const location = { name: 'DomainObjectPage', params: { domainObjectId: this.searchInput } }
        this.$router.push(location)
      }
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
