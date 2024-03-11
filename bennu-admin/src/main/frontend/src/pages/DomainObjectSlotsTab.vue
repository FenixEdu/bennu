<template>
  <div>
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
        <span class="sr-only">
          {{ $t('search.aria-label') }}
        </span>
      </button>
    </form>

    <empty-state v-if="totalItems == 0">
      <template #title>
        <template v-if="hasSearch">
          {{ $t('empty-state.no-slots-search', { search: searchInput }) }}
        </template>
        <template v-else>
          {{ $t('empty-state.no-slots') }}
        </template>
      </template>
    </empty-state>

    <ol
      v-else
      class="card"
    >
      <domain-object-slot-row
        v-for="slot in domainObjectSlots"
        :key="slot.name"
        component="li"
        :domain-object-slot="slot"
      />
    </ol>

    <pagination
      :total-items="totalItems"
      :items-per-page="perPage"
      :current-page="page"
    />
  </div>
</template>

<script>
import Pagination from '@/components/utils/Pagination.vue'
import EmptyState from '@/components/EmptyState.vue'
import DomainObjectSlotRow from '@/components/domain-object/DomainObjectSlotRow.vue'
import { guardWithErrorHandling } from '@/router/guards'

export default {
  components: {
    Pagination,
    EmptyState,
    DomainObjectSlotRow
  },
  beforeRouteUpdate: guardWithErrorHandling(
    async function (to, from, next) {
      this.$progress.set(10)
      await to.meta.beforeRouteLoad(to, from)
      this.searchInput = to.query.q || ''
      this.$progress.complete()
      next()
    }
  ),
  props: {
    query: {
      type: String,
      required: false,
      default: ''
    },
    perPage: {
      type: Number,
      required: true
    },
    totalItems: {
      type: Number,
      required: true
    },
    page: {
      type: Number,
      required: true
    },
    domainObjectSlots: {
      type: Array,
      required: true
    },
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
    return {
      searchInput: this.query
    }
  },
  computed: {
    hasSearch () {
      return this.searchInput !== ''
    }
  },
  methods: {
    performSearch () {
      if (this.searchInput !== this.$route.query.q) {
        const location = { query: { q: this.searchInput === '' ? undefined : this.searchInput } }
        if (this.$route.query.q === undefined) {
          this.$router.replace(location)
        } else {
          this.$router.push(location)
        }
      }
    }
  },
  i18n: {
    messages: {
      pt: {
        search: {
          placeholder: 'Pesquisar...',
          'aria-label': 'Pesquisar'
        },
        'empty-state': {
          'no-slots': 'Não há slots para exibir.',
          'no-slots-search': 'Não há slots para exibir para a busca "{search}".'
        }
      },
      en: {
        search: {
          placeholder: 'Search...',
          'aria-label': 'Search'
        },
        'empty-state': {
          'no-slots': 'There are no slots to display.',
          'no-slots-search': 'There are no slots to display for the search "{search}".'
        }
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.f-search {
  margin-bottom: 1.5rem;
}
</style>
