import { useEffect, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { api } from '../api'
import { Faixa } from '../componentes/Ui'
import { useSessao } from '../sessao'

export default function Enviar() {
  const { setSaldo, saldo } = useSessao()
  const navigate = useNavigate()
  const [alunos, setAlunos] = useState([])
  const [alunoId, setAlunoId] = useState('')
  const [valor, setValor] = useState('')
  const [motivo, setMotivo] = useState('')
  const [erro, setErro] = useState('')
  const [aviso, setAviso] = useState('')
  const [enviando, setEnviando] = useState(false)

  useEffect(() => { document.title = 'Enviar moedas' }, [])

  useEffect(() => {
    let ativo = true
    Promise.all([api('/api/professor'), api('/api/professor/alunos')])
      .then(([professor, lista]) => {
        if (!ativo) return
        setSaldo(professor.saldoMoedas)
        setAviso(professor.aviso || '')
        setAlunos(lista)
      })
      .catch((e) => { if (ativo) setErro(e.message) })
    return () => { ativo = false }
  }, [setSaldo])

  async function enviar(event) {
    event.preventDefault()
    setEnviando(true)
    setErro('')
    try {
      const resposta = await api('/api/professor/envios', {
        method: 'POST',
        json: { alunoId: Number(alunoId), valor: Number(valor), motivo },
      })
      setSaldo(resposta.saldoMoedas)
      navigate('/professor/extrato', { state: { mensagem: resposta.mensagem } })
    } catch (e) {
      setErro(e.message)
      if (e.aviso) setAviso(e.aviso)
      setEnviando(false)
    }
  }

  return (
    <>
      <Faixa erro={erro} aviso={aviso} />
      <h1>Enviar moedas</h1>
      {saldo != null ? <p className="saldo"><span>{saldo}</span><small>moedas</small></p> : null}
      <form onSubmit={enviar}>
        <div className="campo">
          <label htmlFor="alunoId">Aluno</label>
          <select id="alunoId" value={alunoId} required onChange={(event) => setAlunoId(event.target.value)}>
            <option value="">Selecione</option>
            {alunos.map((aluno) => (
              <option key={aluno.id} value={aluno.id}>{aluno.nome}, {aluno.curso}, {aluno.instituicaoNome}</option>
            ))}
          </select>
        </div>
        <div className="campo">
          <label htmlFor="valor">Quantidade</label>
          <input id="valor" type="number" min="1" required value={valor} onChange={(event) => setValor(event.target.value)} />
        </div>
        <div className="campo">
          <label htmlFor="motivo">Motivo</label>
          <textarea id="motivo" maxLength={500} required value={motivo} onChange={(event) => setMotivo(event.target.value)} />
        </div>
        <div className="acoes">
          <button className="botao" type="submit" disabled={enviando || alunos.length === 0}>Enviar</button>
          <Link className="botao secundario" to="/professor">Cancelar</Link>
        </div>
        {alunos.length === 0 ? <p>Nenhum aluno cadastrado ainda.</p> : null}
      </form>
    </>
  )
}
