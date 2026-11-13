import { createContext, useCallback, useContext, useEffect, useState } from 'react'
import { api } from './api'

const SessaoContext = createContext(null)

export function SessaoProvider({ children }) {
  const [sessao, setSessao] = useState(undefined)
  const [saldo, setSaldo] = useState(null)

  const carregar = useCallback(async () => {
    try {
      setSessao(await api('/api/sessao'))
    } catch {
      setSessao(null)
      setSaldo(null)
    }
  }, [])

  useEffect(() => {
    carregar()
  }, [carregar])

  async function sair() {
    const resposta = await api('/api/sessao', { method: 'DELETE' })
    setSessao(null)
    setSaldo(null)
    return resposta.mensagem
  }

  return (
    <SessaoContext.Provider value={{ sessao, setSessao, saldo, setSaldo, sair }}>
      {children}
    </SessaoContext.Provider>
  )
}

export function useSessao() {
  return useContext(SessaoContext)
}
