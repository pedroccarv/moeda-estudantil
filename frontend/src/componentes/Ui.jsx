import { Navigate } from 'react-router-dom'
import { useSessao } from '../sessao'
import { destino } from '../util'

export function Exige({ papel, children }) {
  const { sessao } = useSessao()
  if (sessao === undefined) return <p className="carregando">Carregando.</p>
  if (!sessao) return <Navigate to="/entrar" replace />
  if (sessao.papel !== papel) return <Navigate to={destino(sessao.papel)} replace />
  return children
}

export function Faixa({ ok, erro, aviso }) {
  if (!ok && !erro && !aviso) return null
  return (
    <div>
      {ok ? <p className="aviso">{ok}</p> : null}
      {aviso ? <p className="aviso">{aviso}</p> : null}
      {erro ? <p className="erro-faixa" role="alert">{erro}</p> : null}
    </div>
  )
}

export function Campo({ id, rotulo, tipo = 'text', valor, aoMudar, obrigatorio, placeholder, autoComplete, min, maxLength }) {
  return (
    <div className="campo">
      <label htmlFor={id}>{rotulo}</label>
      <input
        id={id}
        name={id}
        type={tipo}
        value={valor}
        onChange={(event) => aoMudar(event.target.value)}
        required={obrigatorio}
        placeholder={placeholder}
        autoComplete={autoComplete}
        min={min}
        maxLength={maxLength}
      />
    </div>
  )
}

export function Foto({ url, alt }) {
  if (url) return <img src={url} alt={alt} />
  return <div className="sem-foto" role="img" aria-label={alt} />
}
